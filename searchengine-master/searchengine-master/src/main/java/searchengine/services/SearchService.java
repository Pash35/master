package searchengine.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.LemmaErrorLogger;
import searchengine.config.LemmasFromText;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchResponse;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexsServise indexsServise;
    private final int maxFrequency = 30;

    public SearchResponse search (String query, int offset, int limit, String site)  {
        SearchResponse response = new SearchResponse();//ответный класс
        List<SearchData> dataList = new ArrayList<>();//список классов
        List<Integer> listIdPage = new ArrayList<>();//список ид страниц, на которых присутствуют леммы из запроса
        List<Integer> listIdLemma = new ArrayList<>();//список ид лемм , которые присутствуют в запросе

        try {
            LemmasFromText lemmas = new LemmasFromText();
            //создаем из запроса список лемм
            HashMap<String, Integer> lemmaMap = lemmas.mapLemmas(query);
            List<String> listLemma;
            HashMap<String, Integer> resultLemma = new HashMap<>();
            for (Map.Entry<String, Integer> lemma: lemmaMap.entrySet()) {
                String lemmaTemp = lemma.getKey();
                //считаем в базе количество страниц где встречается лемма
                Integer frequency;
                if (site == null) {
                     frequency = lemmaService.findFrequencyLemma(lemmaTemp);
                } else frequency = lemmaService.findFrequencyLemmaBySiteId(lemmaTemp, site);
                //максимум 30, ограничиваем
                if (frequency != 0 && frequency <= maxFrequency) {
                    //фильтруем по возрастанию количества лемм
                    resultLemma.put(lemmaTemp, frequency );
                }

            }
            // сортируем по встречаемости ллеммы
            listLemma = resultLemma.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(l->l.getKey())
                    .toList();

            //если лист не пустой
            if (!listLemma.isEmpty()) {
                int count = 0;
                response.setResult(true);
                float relevan = 0;
                //количество найденных в базе лемм...
                for (int i = 0 ; i < listLemma.size(); i++) {
                    //... для них находим в базе страницы и данные сайтов

                    List<Integer> listIdLemmaTemp;
                    if (site == null) {//по всем сайтам ищем ид лемм, иначе по одному
                       listIdLemmaTemp =lemmaService.findIdByLemma(listLemma.get(i));
                    } else listIdLemmaTemp = lemmaService.findIdByLemmaAndSiteId(listLemma.get(i), site);
                    listIdLemma.addAll(listIdLemmaTemp);
                    if (!listIdPage.isEmpty()) {//поиск страниц с леммами
                        for (Integer lemmaTemp : listIdLemmaTemp) {
                            //проверяем совпадение страниц от первой леммы
                            List<Integer> tempInteger = pageService.findIdByLemmaId(lemmaTemp);
                            for (Integer tempIn : tempInteger) {
                                if (!listIdPage.contains(tempIn)) {
                                    listIdPage.add(tempIn);
                                }
                            }
                        }
                    } else {
                        //поиск страниц по первой лемме
                        for (Integer lemmaTemp : listIdLemmaTemp) {
                            listIdPage.addAll(pageService.findIdByLemmaId(lemmaTemp));
                        }
                    }
                }
                //формируем ответ
                for (Integer tempPage : listIdPage) {
                    PageEntity page = pageService.findById(tempPage);
                    SiteEntity siteEntity = siteService.getById(page.getSiteId().getId()).get();
                    //заполняем ответный класс
                    SearchData data = new SearchData();
                    String siteUrl = siteEntity.getUrl();
                    data.setSite(siteUrl.substring(0,siteUrl.length() - 1));
                    data.setUri(page.getPath());
                    data.setSiteName(siteEntity.getName());
                    data.setTitle(titleSite(page.getContent()));//вырезаем title
                    //расчитываем релевантность по каждой лемме ..
                    List<Float> ranks = new ArrayList<>();
                    Float rankResult = 0f;
                    for (Integer tempLemma : listIdLemma) {
                        Float rank = indexsServise.findRanksByPageIdAndLemmaId(tempPage, tempLemma);
                        if (rank != null) {
                            ranks.add(rank);
                        } else  ranks.add(0f);
                    }
                    //..и временно записываем в релевантность ответного класса
                    for (Float tempRanks: ranks) {
                        rankResult  = rankResult  + tempRanks;
                    }
                    data.setRelevance(rankResult);
                    relevan = Math.max(relevan, rankResult);//максимальная релевантность
                    data.setSnippet(snippetSite(page.getContent(), query));//находим текст где встречаются слова
                    dataList.add(data);
                    count = count + 1;
                }
                //пересчитываем по максимальному числу релевантности и тут же переписываем
                for (SearchData data: dataList) {
                    data.setRelevance(data.getRelevance() / relevan);
                }
                //сортируем по максимальной релевантности
                Comparator<SearchData> compareByRelevance = Comparator
                        .comparing(SearchData::getRelevance);
                ArrayList<SearchData> sortedList = dataList.stream()
                        .sorted(compareByRelevance.reversed()).collect(Collectors.toCollection(ArrayList::new));
                response.setData(sortedList);
                response.setCount(count);

            } else {//если слова не найдены формируем нулевой ответ
                response.setResult(false);
                response.setCount(0);
                response.setData(null);
            }

        } catch (Exception e) {
            new LemmaErrorLogger("Error in class SearchService");
        }
        return response;
    }


    private String titleSite (String text) {
        int start = text.indexOf("<title>") + 7;
        int end = text.indexOf("</title>");
        return text.substring(start, end);
    }
    private String snippetSite(String text, String findText) throws IOException {
        StringBuilder builder = new StringBuilder();
        List<String> stringList = new ArrayList<>();
        int startFindText = 0;
        //разбираем на отдельные слова
        String[] words = findText.toLowerCase()
                .replaceAll("([^а-я\\s])", " ")
                .trim().split("\s+");
        for (String word : words) {
            String tempWord = word;
            //поиск по запросу отдельного слова...
            startFindText = text.indexOf(tempWord);
            //..иначе по лемме слова..
            if (startFindText == -1) {
                LemmasFromText newText = new LemmasFromText();
                for (String lemm : newText.listLemma(tempWord)) {
                    startFindText = text.indexOf(lemm);
                    if (startFindText != -1) {
                        tempWord = lemm;
                        break;
                    }
                }
                //..иначе по 90 % букв в слове..
                if (startFindText == -1) {
                    tempWord = tempWord.substring(0,tempWord.length()*90/100);
                    startFindText = text.indexOf(tempWord);
                    //..иначе без первой буквы (если с большой)
                    if (startFindText == -1) {
                        tempWord = tempWord.substring(1, tempWord.length());
                        startFindText = text.indexOf(tempWord);
                    }
                }
                //если слово не найдено
                if (startFindText == -1) {
                    builder.append("\n не найдено: <b>" + word+ "</b>");
                    continue;
                }
            }
            //если слово из запроса найдено
            if (startFindText != -1) {
                int s = text.lastIndexOf(">", startFindText);
                int e = text.indexOf("<", startFindText);
                String temp = text.substring(s + 1, e);
                if (stringList.contains(temp)) {
                    int si = builder.indexOf(tempWord);
                    builder.replace(si, si + tempWord.length(), "<b>" + tempWord + "</b>");

                    continue;
                }
                stringList.add(temp);
                String temp1 = temp.replace(tempWord, "<b>" + tempWord + "</b>");
                builder.append("\r\n");

                builder.append(temp1 + " \n");
                builder.append("\r\n");
            }
        }
        return builder.toString();
    }
}
