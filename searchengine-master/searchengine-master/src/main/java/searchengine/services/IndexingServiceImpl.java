package searchengine.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.threads.SiteThread;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.util.*;
@Service
@Transactional
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService{

    private final SitesList sitesList;
    private final SiteService siteService;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private List<Thread> listThread = new ArrayList<>();

    @Override
    public IndexingResponse startIndexing () {

        IndexingResponse response = new IndexingResponse();

        //Считаем завершенные потоки
        int count = 0;
        for (Thread thread: listThread) {
            if(thread.getState()==Thread.State.TERMINATED) {
               count++;
            }
        }
        //Проверяем завершились ли потоки сайтов, если все, то обнуляем лист
        if(count == listThread.size() && count > 0) {
           listThread.clear();
        }
        //Если потоки в работе, возвращаем сообщение об ошибке
        if(listThread.isEmpty()) {
            //потоков в работе нет, запускаем цикл удаления, обновления и парсинга сайтов
            for (Site ref : sitesList.getSites()) {
                String tempRef = validationRef(ref.getUrl());
                ref.setUrl(tempRef);
                indexRepository.deleteByUrl(tempRef);
                pageRepository.deleteByUrl(tempRef);
                lemmaRepository.deleteByUrl(tempRef);
                siteRepository.deleteByUrl(tempRef);
                siteService.create(ref);
                SiteThread siteThread = new SiteThread(siteService);
                siteThread.setRef(tempRef);
                Thread thread = new Thread(siteThread);
                thread.start();
                listThread.add(thread);
                response.setResult(true);
            }
        } else response.setError("Индексация уже запущена");

        return response;
    }

    @Override
    public IndexingResponse stopIndexing() {
        IndexingResponse response = new IndexingResponse();
        //если лист потоков не пустой, т.е. парсинг еще в работе, говорим потокам остановиться
        if (!listThread.isEmpty()) {
            for (Thread thread : listThread) {
                thread.interrupt();
            }
            response.setResult(true);
            listThread.clear();
        } else response.setError("Индексация не запущена");
        return response;
    }

    @Override
    public IndexingResponse indexPage(String ref) {//индексация отдельной страницы
        IndexingResponse response = new IndexingResponse();
        String refSite = validationRef(ref);
        Site site = sitesList.getSites()
                .stream()
                .filter(s -> s.getUrl().equals(refSite))
                .findFirst()
                .orElse(null);

        //проеряем есть сайт в списке
        if(site != null) {
            indexRepository.deleteByUrl(refSite);
            pageRepository.deleteByUrl(refSite);
            lemmaRepository.deleteByUrl(refSite);
            siteRepository.deleteByUrl(refSite);
            siteService.create(site);
            SiteThread siteThread = new SiteThread(siteService);
            siteThread.setRef(refSite);
            Thread thread = new Thread(siteThread);
            thread.start();
            response.setResult(true);
        } else {//если нет выдаем ответ и заносим в список
            Site siteNew = new Site();
            siteNew.setUrl(refSite);
            siteNew.setName(refSite);
            sitesList.getSites().add(siteNew);
            response.setError("Данная страница находится за пределами сайтов, \n" +
                    "указанных в конфигурационном файле\n");
        }
        return response;
    }

    private String validationRef(String ref) {
        //находим ссылку на сайт
        int index = ref.indexOf("http");
        String retRef = ref.substring(index, ref.length());
        String regex = "https?://[^,\s]+/{1}";

        //проверяем на правильность, добавляем слеш
        if(!retRef.matches(regex)) {
            retRef = retRef.concat("/");
        }

        return retRef;
    }
}
