package Bubble.bubblog.domain.tag.entity;

import Bubble.bubblog.domain.post.entity.BlogPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post_tag", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "tag_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {    // blog_post <-> tag 다대다 관계 테이블
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_posttag_post"))
    private BlogPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false, foreignKey = @ForeignKey(name = "fk_posttag_tag"))
    private Tag tag;

    public PostTag(BlogPost post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }
}
