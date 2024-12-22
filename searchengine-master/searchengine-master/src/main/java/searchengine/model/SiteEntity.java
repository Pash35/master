package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "site")
public class SiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')")
    private Status status;

    @CreationTimestamp
    @Column(name = "status_time", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "url", columnDefinition = "VARCHAR(255)")
    private String url;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "siteId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PageEntity> pages;

    @OneToMany(mappedBy = "siteId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LemmaEntity> lemmas;

}
