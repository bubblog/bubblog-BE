package Bubble.bubblog.domain.comment.entity;

import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// @SQLDelete(sql="UPDATE comment SET is_deleted = true, deleted_at = NOW() WHERE id = ?")  // repository.delete()를 호출해도 soft하게 삭제 될 수 있게 보장. 현재는 클래스에 softDelete가 내부 메서드로 있기 때문에 굳이 없어도 됨.
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    // Many to one은 기본은 Eager 전략 -> Lazy로 설정해서 불필요한 Join을 막음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private BlogPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 대댓글 구조를 위한 자기 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Comment> children = new ArrayList<>();

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 객체 생성 메서드 (팩토리 메서드)
    public static Comment createComment(String content, BlogPost post, User user) {
        Comment comment = new Comment();
        comment.content = content;
        comment.post = post;
        comment.user = user;
        comment.parent = null; // 최상위 댓글
        return comment;
    }

    // 대댓글 생성 메서드
    public static Comment createReply(String newContent, BlogPost post, User user, Comment parent) {
        Comment comment = new Comment();
        comment.content = newContent;
        comment.post = post;
        comment.user = user;
        comment.parent = parent;

        parent.children.add(comment);
        return comment;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // 복원 메서드 (관리자용)
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

}