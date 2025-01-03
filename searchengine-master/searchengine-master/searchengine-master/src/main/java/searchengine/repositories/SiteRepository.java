package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteEntity;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<SiteEntity, Integer> {

    @Query(value = "SELECT * FROM site s WHERE s.url = ?1%", nativeQuery = true)
    SiteEntity findByUrl(String url);

    @Modifying
    @Query(value = "DELETE FROM site s WHERE s.url LIKE ?1%", nativeQuery = true)
    void deleteByUrl(String url);


    @Query(value = "SELECT * FROM site", nativeQuery = true)
    List<SiteEntity> findSite();

}
