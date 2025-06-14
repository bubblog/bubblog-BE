package Bubble.bubblog.domain.post.repository;

import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.post.entity.QBlogPost;
import Bubble.bubblog.global.util.queryDSL.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BlogPostRepositoryCustomImpl implements BlogPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 전체 게시글을 DB에서 조건절을 통헤 불러와서 정렬
    @Override
    public Page<BlogPost> searchPosts(String keyword, Pageable pageable) {
        QBlogPost blogPost = QBlogPost.blogPost;
        BooleanBuilder builder = new BooleanBuilder().and(blogPost.publicVisible.isTrue());

        // 검색 - 제목, 내용, 요약에 대해
        if (StringUtils.hasText(keyword)) {
            builder.and(blogPost.title.containsIgnoreCase(keyword)
                    .or(blogPost.content.containsIgnoreCase(keyword))
                    .or(blogPost.summary.containsIgnoreCase(keyword)));
        }

        JPAQuery<BlogPost> query = queryFactory.selectFrom(blogPost).where(builder);
        JPAQuery<Long> countQuery = queryFactory.select(blogPost.count()).from(blogPost).where(builder);

        QueryDslUtils.applySorting(query, pageable, BlogPost.class, "blogPost");

        return QueryDslUtils.getPageResult(query, countQuery, pageable);
    }

    // 특정 사용자 게시글을 조건절을 통해 불러와서 정렬
    @Override
    public Page<BlogPost> searchUserPosts(UUID userId, boolean isOwner, List<Long> categoryIds, Pageable pageable) {
        QBlogPost post = QBlogPost.blogPost;
        BooleanBuilder builder = new BooleanBuilder().and(post.user.id.eq(userId));

        if (!isOwner) {
            builder.and(post.publicVisible.isTrue());
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            builder.and(post.category.id.in(categoryIds));
        }

        JPAQuery<BlogPost> query = queryFactory.selectFrom(post).where(builder);
        JPAQuery<Long> countQuery = queryFactory.select(post.count()).from(post).where(builder);

        QueryDslUtils.applySorting(query, pageable, BlogPost.class, "blogPost");
        return QueryDslUtils.getPageResult(query, countQuery, pageable);
    }
}
