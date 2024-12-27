package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexEntity;

@Repository
public interface IndexRepository extends JpaRepository<IndexEntity, Integer> {

    @Query(value = "SELECT ranks FROM indexs WHERE page_id = ?1% AND lemma_id = ?2%", nativeQuery = true)
    Float findRanks(int pageId, int lemmaId);

    @Modifying
    @Query(value = "DELETE FROM indexs WHERE page_id in (SELECT id FROM page WHERE site_id in (SELECT id FROM site WHERE url LIKE ?1%))", nativeQuery = true)
    void deleteByUrl(String url);
}
