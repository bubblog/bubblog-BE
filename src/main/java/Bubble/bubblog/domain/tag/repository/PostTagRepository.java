package Bubble.bubblog.domain.tag.repository;

import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.tag.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findAllByPost(BlogPost post);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM PostTag pt WHERE pt.post = :post")
    void deleteByPost(BlogPost post);
}
