package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.PageRepository;

@Service
@RequiredArgsConstructor
public class IndexsServise {
    private final IndexRepository indexRepository;

    public Float findRanksByPageIdAndLemmaId(int pageId, int lemmaId) {
        return indexRepository.findRanks(pageId, lemmaId);
    }
    public void deleteByUrl(String url) {
        indexRepository.deleteByUrl(url);
    }
}
