package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.repositories.LemmaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LemmaService {
    private final LemmaRepository lemmaRepository;

    public void deleteByUrl(String url) {
        lemmaRepository.deleteByUrl(url);
    }
    public Integer findCountByUrl(String url) {
        return lemmaRepository.findCountByUrl(url);
    }
    public Integer findFrequencyLemma(String query) {
        return lemmaRepository.findFrequencyLemma(query);
    }
    public Integer findFrequencyLemmaBySiteId(String query, String site) {
        return lemmaRepository.findFrequencyLemmaBySiteId(query, site);
    }

    public List<Integer> findIdByLemma(String lemma) {
        return lemmaRepository.findIdByLemma(lemma);
    }
    public List<Integer> findIdByLemmaAndSiteId(String lemma, String site) {
        return lemmaRepository.findIdByLemmaAndSiteId(lemma, site);
    }
}
