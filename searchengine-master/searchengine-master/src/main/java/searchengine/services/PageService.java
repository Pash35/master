package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.PageEntity;
import searchengine.repositories.PageRepository;

import java.util.Collection;
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
    public List<PageEntity> findBySiteId(int id) {
        return pageRepository.findBySiteId(id);
    }

    public List<Integer> findIdByLemmaId(int id) {
        return pageRepository.findIdByLemmaId(id);
    }
    public Object getById(Integer id) {
        return null;
    }


    public Collection getAll() {
        return null;
    }


    public void create(Object item) {

    }


    public void update(Object item) {

    }


    public void delete(Integer id) {

    }
}
