package Bubble.bubblog.domain.post.service;

import Bubble.bubblog.domain.post.entity.BlogPost;
import org.springframework.data.jpa.domain.Specification;

// JPA Specification
public class BlogPostSpecifications {
    public static Specification<BlogPost> publicVisible() {
        return (root, query, cb) -> cb.isTrue(root.get("publicVisible"));
    }

    public static Specification<BlogPost> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<BlogPost> contentContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<BlogPost> summaryContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("summary")), "%" + keyword.toLowerCase() + "%");
    }
}
