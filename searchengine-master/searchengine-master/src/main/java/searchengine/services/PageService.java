package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.PageEntity;
import searchengine.repositories.PageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;

    public void deleteByUrl(String url) {
        pageRepository.deleteByUrl(url);
    }
    public Integer findCountByUrl(String url) {
        return pageRepository.findCountByUrl(url);
    }
    public PageEntity findById(int id) {
        return pageRepository.findById(id);
    }

    public List<Integer> findIdByLemmaId(int id) {
        return pageRepository.findIdByLemmaId(id);
    }

}
