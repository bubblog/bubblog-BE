package Bubble.bubblog.domain.post.service;

import Bubble.bubblog.domain.category.entity.Category;
import Bubble.bubblog.domain.category.repository.CategoryRepository;
import Bubble.bubblog.domain.post.dto.req.BlogPostRequestDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.repository.BlogPostRepository;
import Bubble.bubblog.domain.tag.repository.PostTagRepository;
import Bubble.bubblog.domain.tag.repository.TagRepository;
import Bubble.bubblog.domain.user.dto.req.SignupRequestDTO;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional   // 테스트 종료 후 자동 롤백
@DisplayName("BlogPostService 통합 테스트")
class BlogPostServiceTest {
    // 의존성 주입
    @Autowired private BlogPostService blogPostService;
    @Autowired private UserRepository userRepository;
    @Autowired private BlogPostRepository blogPostRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PostTagRepository postTagRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("게시글 생성 시 태그도 함께 생성된다")
    public void createPost() {
        // given
        SignupRequestDTO signupDTO = new SignupRequestDTO();
        signupDTO.setEmail("test@bubblog.com");
        signupDTO.setPassword("password123!");
        signupDTO.setNickname("tester");

        User user = userRepository.save(User.from(signupDTO, passwordEncoder));
        Category category = categoryRepository.save(Category.of("테스트 카테고리", user));

        BlogPostRequestDTO request = BlogPostRequestDTO.builder()
                .title("제목")
                .content("내용")
                .summary("요약")
                .publicVisible(true)
                .thumbnailUrl("thumbnail.jpg")
                .categoryId(category.getId())
                .tags(List.of("Java", "Spring"))
                .build();

        // when
        BlogPostDetailDTO result = blogPostService.createPost(request, user.getId());

        // then
        Assertions.assertEquals("제목", result.getTitle());
        Assertions.assertTrue(result.getTags().containsAll(List.of("Java", "Spring")));
        Assertions.assertEquals(2, tagRepository.findAll().size());
        Assertions.assertEquals(2, postTagRepository.findAll().size());
    }

    @Test
    public void getPost_withTag() {
    }


    @Test
    void getPostsByTagName() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void updatePost() {
    }
}