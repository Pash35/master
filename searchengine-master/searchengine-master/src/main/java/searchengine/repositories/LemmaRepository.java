package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.LemmaEntity;

import java.util.List;


@Repository
public interface LemmaRepository extends JpaRepository<LemmaEntity, Integer> {

    @Modifying
    @Query(value = "DELETE FROM lemma WHERE site_id = (SELECT id FROM site WHERE url LIKE ?1%)", nativeQuery = true)
    void deleteByUrl(String url);

    @Query(value = "SELECT count(*) FROM lemma WHERE site_id = (SELECT id FROM site WHERE url = ?1%)", nativeQuery = true)
    Integer findCountByUrl(String url);

    @Query(value = "SELECT * FROM lemma WHERE site_id = ?1%", nativeQuery = true)
    List<LemmaEntity> findBySiteId(int id);
    @Query(value = "SELECT CASE WHEN SUM(frequency) IS NULL THEN 0 ELSE SUM(frequency) END FROM lemma WHERE lemma = ?1%", nativeQuery = true)
    int findFrequencyLemma(String query);

    @Query(value = "SELECT CASE WHEN SUM(frequency) IS NULL THEN 0 ELSE SUM(frequency) END FROM lemma WHERE lemma = ?1% AND site_id = (SELECT id FROM site WHERE url = ?2%)", nativeQuery = true)
    Integer findFrequencyLemmaBySiteId(String query, String site);

    @Query(value = "SELECT id FROM lemma WHERE lemma = ?1%", nativeQuery = true)
    List<Integer> findIdByLemma(String lemma);

    @Query(value = "SELECT id FROM lemma WHERE lemma = ?1% AND site_id = (SELECT id FROM site WHERE url = ?2%)", nativeQuery = true)
    List<Integer> findIdByLemmaAndSiteId(String lemma, String site);
}
