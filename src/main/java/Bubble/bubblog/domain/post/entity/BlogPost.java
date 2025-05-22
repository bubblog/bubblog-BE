package Bubble.bubblog.domain.post.entity;

import Bubble.bubblog.domain.category.entity.Category;
import Bubble.bubblog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT", length = 255)
    private String summary;

    @Column(name = "is_public", nullable = false, columnDefinition = "boolean default false")
    private boolean publicVisible;

    @Column(name = "thumbnail_url", nullable = true)
    private String thumbnailUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Keyword> keywords = new ArrayList<>();

    private BlogPost(String title, String content, String summary,
                     boolean isPublic, String thumbnailUrl,
                     User user, Category category) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.publicVisible = isPublic;
        this.thumbnailUrl = thumbnailUrl;
        this.user = user;
        this.category = category;
    }

    public static BlogPost of(String title, String content, String summary,
                              boolean isPublic, String thumbnailUrl,
                              User user, Category category) {
        return new BlogPost(title, content, summary, isPublic, thumbnailUrl, user, category);
    }

    // 버블로그 게시글 수정용
    public void update(String title, String content, String summary, boolean isPublic,
                       String thumbnailUrl, Category category) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.publicVisible = isPublic;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
    }
}
