package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "page",  indexes = @Index(columnList = "path"))
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteEntity siteId;

    @Column(name = "path", columnDefinition = "VARCHAR(255)")
    private String path;

    @Column(name = "code")
    private Integer code;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    @OneToMany(mappedBy = "pageId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndexEntity> indexes;
}
