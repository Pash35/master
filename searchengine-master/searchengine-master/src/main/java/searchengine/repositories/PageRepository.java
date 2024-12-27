package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.PageEntity;
<<<<<<< HEAD
=======
import searchengine.model.SiteEntity;
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Integer> {

    @Modifying
    @Query(value = "DELETE FROM page WHERE site_id = (SELECT id FROM site WHERE url = ?1%)", nativeQuery = true)
    void deleteByUrl(String url);

    @Query(value = "SELECT count(*) FROM page WHERE site_id = (SELECT id FROM site WHERE url = ?1%)", nativeQuery = true)
    Integer findCountByUrl(String url);

    @Query(value = "SELECT * FROM page WHERE id =  ?1%", nativeQuery = true)
<<<<<<< HEAD
    PageEntity findByIdField(int id);
=======
    PageEntity findById(int id);
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb

    @Query(value = "SELECT * FROM page WHERE site_id =  ?1%", nativeQuery = true)
    List<PageEntity> findBySiteId(int id);
    @Query(value = "select p.id\n" +
            "from page as p join indexs as i on p.id = i.page_id\n" +
            "where lemma_id =?1%", nativeQuery = true)
    List<Integer> findIdByLemmaId(int id);
}
