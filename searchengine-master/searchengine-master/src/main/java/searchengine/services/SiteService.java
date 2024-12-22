package searchengine.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.LemmaErrorLogger;
import searchengine.config.LemmasFromText;
import searchengine.config.Site;
import searchengine.model.*;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    List<SiteEntity> findSite()  {
        return siteRepository.findSite();
    }
    public SiteEntity findByUrl(String url) {
        return siteRepository.findByUrl(url);
    }

    public void deleteByUrl(String url) {
        siteRepository.deleteByUrl(url);
    }
    public Optional<SiteEntity> getById(Integer id) {
        return siteRepository.findById(id);
    }



    //Создаем запись в таблице site
    public void create(Site item) {
        SiteEntity site = new SiteEntity();
        site.setName(item.getName());
        site.setUrl(item.getUrl());
        site.setStatus(Status.INDEXING);
        siteRepository.save(site);

    }

    public void updateError(String url, String textError) {
        SiteEntity site = siteRepository.findByUrl(url);
        site.setStatus(Status.FAILED);
        site.setLastError(textError);
        siteRepository.save(site);

    }


    public void update(String url, HashMap<String, PageEntity> page) throws IOException {
        SiteEntity site = siteRepository.findByUrl(url);
        List<PageEntity> pageList = new ArrayList<>();
        List<LemmaEntity> lemmaList = new ArrayList<>();

        HashMap<String, Integer> lemmaFrequency = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> lemmaRank = new HashMap<>();
        //заносим данные в таблицы site, page, lemma
        for (Map.Entry<String, PageEntity> entry : page.entrySet()) {
            //ссылка без базового адреса

            entry.getValue().setPath(entry.getKey().replace(url, "/"));
            entry.getValue().setSiteId(site);

            try {
                LemmasFromText lemmas = new LemmasFromText();
                HashMap<String, Integer> lemmaMap = lemmas.mapLemmas(entry.getValue().getContent());
                lemmaRank.put(entry.getValue().getPath(), lemmaMap);
                for (Map.Entry<String, Integer> temp : lemmaMap.entrySet()) {

                    int frequency = 1;

                    if (lemmaFrequency.containsKey(temp.getKey())) {
                        frequency = lemmaFrequency.get(temp.getKey());
                        lemmaFrequency.put(temp.getKey(), frequency + 1);

                    } else
                    {
                        lemmaFrequency.put(temp.getKey(), frequency);

                    }
                }

            } catch (IOException e) {
                new LemmaErrorLogger("Error in class SiteService");
            }

            pageList.add(entry.getValue());
        }

        for (Map.Entry<String, Integer> temp : lemmaFrequency.entrySet()) {
            LemmaEntity lemma = new LemmaEntity();
            lemma.setLemma(temp.getKey());
            lemma.setFrequency(temp.getValue());
            lemma.setSiteId(site);
            lemmaList.add(lemma);
        }
        site.setLemmas(lemmaList);
        site.setPages(pageList);
        siteRepository.save(site);
        lemmaList.clear();
        pageList.clear();
        site = siteRepository.findByUrl(url);

        pageList = pageRepository.findBySiteId(site.getId());
        lemmaList = lemmaRepository.findBySiteId(site.getId());
        for(PageEntity tempPage: pageList) {//заносим в таблицу indexs
            List<IndexEntity> indexList = new ArrayList<>();
            for(LemmaEntity templemma: lemmaList) {
                for (Map.Entry<String, HashMap<String, Integer>> tempIndex : lemmaRank.entrySet()) {

                     for (Map.Entry<String, Integer> temp : tempIndex.getValue().entrySet()) {
                            if(tempIndex.getKey().equals(tempPage.getPath()) && temp.getKey().equals(templemma.getLemma())) {
                                IndexEntity index = new IndexEntity();
                                index.setRanks((float)temp.getValue()/ templemma.getFrequency() );
                                index.setLemmaId(templemma);
                                index.setPageId(tempPage);
                                indexList.add(index);
                            }
                     }
                }
                templemma.setIndexes(indexList);
            }
            tempPage.setIndexes(indexList);
        }

        site.setLemmas(lemmaList);
        site.setPages(pageList);
        site.setStatus(Status.INDEXED);
        siteRepository.save(site);

    }
}
