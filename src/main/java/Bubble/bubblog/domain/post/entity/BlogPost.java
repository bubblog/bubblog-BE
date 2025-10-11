package Bubble.bubblog.domain.post.entity;

import Bubble.bubblog.domain.category.entity.Category;
import Bubble.bubblog.domain.comment.entity.Comment;
import Bubble.bubblog.domain.tag.entity.PostTag;
import Bubble.bubblog.domain.tag.entity.Tag;
import Bubble.bubblog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 게시글 엔티티
@Entity
@Table(name = "blog_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
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
    private boolean publicVisible = false;

    @Column(name = "thumbnail_url", nullable = true)
    private String thumbnailUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)  // Soft delete 이므로 CASCADE 설정 X
    private List<Comment> comments;


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
        this.viewCount = 0L;
        this.likeCount = 0L;
    }

    public static BlogPost of(String title, String content, String summary,
                              boolean isPublic, String thumbnailUrl,
                              User user, Category category) {
        return new BlogPost(title, content, summary, isPublic, thumbnailUrl, user, category);
    }

    // 게시글 수정
    public void update(String title, String content, String summary, boolean isPublic,
                       String thumbnailUrl, Category category) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.publicVisible = isPublic;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
    }

    // 게시글 조회수 증가
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    // 게시글 좋아요 증가
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    // 게시글 좋아요 감소
    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount -= 1;
        }
    }

    public void addTag(Tag tag) {
        PostTag postTag = new PostTag(this, tag);
        this.postTags.add(postTag);
        tag.getPostTags().add(postTag); // 양방향 연관관계 유지
    }


}
