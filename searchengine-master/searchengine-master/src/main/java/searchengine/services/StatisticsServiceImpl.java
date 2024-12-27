package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.SiteEntity;
<<<<<<< HEAD
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
=======
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {


    private final SitesList sites;
<<<<<<< HEAD
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
=======
    private final PageService pageService;
    private final SiteService siteService;
    private final LemmaService lemmaService;
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
<<<<<<< HEAD
        List<SiteEntity> siteBaseList = siteRepository.findSite();//проверка в базе данных
=======
        List<SiteEntity> siteBaseList = siteService.findSite();//проверка в базе данных
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb
        List<Site> sitesList = new ArrayList<>();

        if (siteBaseList.isEmpty()) {
            sitesList = sites.getSites();
            total.setSites(sitesList.size());
        } else {
            total.setSites(siteBaseList.size());
            for (SiteEntity se: siteBaseList) {
                Site s = new Site();
                s.setUrl(se.getUrl());
                s.setName(se.getName());
                sitesList.add(s);
            }
            total.setIndexing(true);
        }

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for(int i = 0; i < sitesList.size(); i++) {
            Site site = sitesList.get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();
<<<<<<< HEAD
            SiteEntity siteEntity = siteRepository.findByUrl(site.getUrl());
            if (siteEntity != null) {
                item.setName(site.getName());
                item.setUrl(site.getUrl());
                int pages = pageRepository.findCountByUrl(site.getUrl());
                Integer lemmas = lemmaRepository.findCountByUrl(site.getUrl());
=======
            SiteEntity siteEntity = siteService.findByUrl(site.getUrl());
            if (siteEntity != null) {
                item.setName(site.getName());
                item.setUrl(site.getUrl());
                int pages = pageService.findCountByUrl(site.getUrl());
                Integer lemmas = lemmaService.findCountByUrl(site.getUrl());
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb
                item.setPages(pages);
                item.setLemmas(lemmas);
                item.setStatus(siteEntity.getStatus().toString());
                item.setError(siteEntity.getLastError());
                item.setStatusTime(System.currentTimeMillis());
                total.setPages(total.getPages() + pages);
                total.setLemmas(total.getLemmas() + lemmas);
                detailed.add(item);
            }
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}
