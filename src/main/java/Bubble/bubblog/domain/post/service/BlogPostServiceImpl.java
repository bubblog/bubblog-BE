package Bubble.bubblog.domain.post.service;

import Bubble.bubblog.domain.category.entity.Category;
import Bubble.bubblog.domain.category.repository.CategoryClosureRepository;
import Bubble.bubblog.domain.category.repository.CategoryRepository;
import Bubble.bubblog.domain.comment.repository.CommentRepository;
import Bubble.bubblog.domain.post.dto.req.BlogPostRequestDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.domain.post.dto.res.UserPostsResponseDTO;
import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.post.entity.PostLike;
import Bubble.bubblog.domain.post.repository.BlogPostRepository;
import Bubble.bubblog.domain.post.repository.PostLikeRepository;
import Bubble.bubblog.domain.tag.entity.PostTag;
import Bubble.bubblog.domain.tag.entity.Tag;
import Bubble.bubblog.domain.tag.repository.PostTagRepository;
import Bubble.bubblog.domain.tag.repository.TagRepository;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import Bubble.bubblog.global.service.AiService;
import Bubble.bubblog.global.service.EmbeddingProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryClosureRepository categoryClosureRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final AiService aiService;
    private final CommentRepository commentRepository;
    private final EmbeddingProducer embeddingProducer;

    @Transactional
    @Override
    public BlogPostDetailDTO createPost(BlogPostRequestDTO request, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_CATEGORY_ACCESS));

        List<String> categoryList = categoryClosureRepository.findAncestorNamesByDescendantId(category.getId());

        BlogPost blogPost = BlogPost.of(
                request.getTitle(),
                request.getContent(),
                request.getSummary(),
                request.isPublicVisible(),
                request.getThumbnailUrl(),
                user,
                category
        );

        BlogPost post = blogPostRepository.save(blogPost);

        List<String> tags = request.getTags();   // tag 리스트를 프론트에서 받음

        if (tags != null) {
            for (String tagName : tags) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                postTagRepository.save(new PostTag(post, tag));
            }
        }

        // Redis 큐로 임베딩 요청 전송
        embeddingProducer.sendEmbeddingRequest(post.getId(), true, true);

        return new BlogPostDetailDTO(post, categoryList, tags);
    }

    // 게시글 상세 조회 (공개 게시글만)
    @Transactional(readOnly = true)
    @Override
    public BlogPostDetailDTO getPost(Long postId) {
        BlogPost post = blogPostRepository.findDetailById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.isPublicVisible()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND); // 비공개 게시글임을 명시하지 않고 아예 해당 게시글이 존재하지 않는 것으로 처리
        }

        List<String> categoryList = categoryClosureRepository.findAncestorNamesByDescendantId(post.getCategory().getId());

        List<String> tags = post.getPostTags().stream()    // tag 리스트를 스트림으로 바꿔 각 요소를 반복 처리
                .map(postTag -> postTag.getTag().getName())   // 태그명을 리턴
                .collect(Collectors.toList());   // 스트림을 모아 리스트로 변환

        return new BlogPostDetailDTO(post, categoryList, tags);
    }

    // 모든 게시글 보기
    @Transactional(readOnly = true)
    @Override
    public Page<BlogPostSummaryDTO> getAllPosts(String keyword, Pageable pageable) {
        return blogPostRepository.searchPosts(keyword, pageable)
                .map(BlogPostSummaryDTO::new);
    }


    // 특정 사용자의 게시글 목록 조회
    @Transactional(readOnly = true)
    @Override
    public UserPostsResponseDTO getPostsByUser(UUID targetUserId, Long categoryId, Pageable pageable) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Long> categoryIds;
        if (categoryId != null) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            categoryIds = categoryClosureRepository.findAllSubtreeIdsIncludingSelf(categoryId);
        } else {
            categoryIds = null;
        }

        Page<BlogPost> posts = blogPostRepository
                .searchUserPosts(targetUserId, false, categoryIds, pageable);   // isOwner을 false로 고정 -> 공개 게시글만 조회하니까

        return new UserPostsResponseDTO(
                user.getId(),
                user.getNickname(),
                posts.map(BlogPostSummaryDTO::new).getContent(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.hasNext()
        );
    }

    // 인증된 사용자가 자신이 좋아요 누른 게시글 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> getLikedPosts(UUID userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return postLikeRepository.findLikedPostsByUser(userId, pageable)
                .map(BlogPostSummaryDTO::new);
    }
    
    // 태그 기반 게시글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> getPostsByTagId(Long tagId, Pageable pageable) {
        return blogPostRepository.findPublicPostsByTagId(tagId, pageable)
                .map(BlogPostSummaryDTO::new);
    }

    // 게시글 삭제
    @Transactional
    @Override
    public void deletePost(Long postId, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        blogPostRepository.delete(post);

    }

    // 게시글 수정
    @Transactional
    @Override
    public BlogPostDetailDTO updatePost(Long postId, BlogPostRequestDTO request, UUID userId) {
        BlogPost post = blogPostRepository.findDetailById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_CATEGORY_ACCESS));

        List<String> categoryList = categoryClosureRepository.findAncestorNamesByDescendantId(category.getId());

        // 변경 전 값 저장
        String oldTitle   = post.getTitle();
        String oldContent = post.getContent();

        // 변경 여부 판단
        boolean titleChanged   = !Objects.equals(oldTitle, request.getTitle());
        boolean contentChanged = !Objects.equals(oldContent, request.getContent());

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getSummary(),
                request.isPublicVisible(),
                request.getThumbnailUrl(),
                category
        );

        // 기존 태그 관계를 모두 끊음
        post.getPostTags().clear();

        // flush()로 예약 되어 있던 clear()명령어를 바로 처리
        // 처리하지 않으면 clear()를 예약한 상태에서 Insert가 먼저 진행되고,
        // 만약 tag 내용의 변화가 없을 때 수정된 게시글 Insert 시 (postId, tagId)키값의 중복 제약이 걸림
        blogPostRepository.flush();

        // 요청받은 태그 리스트를 가져옴
        List<String> tags = request.getTags();

        // 새 태그 저장
        if (tags != null) {
            for (String tagName : tags) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                post.addTag(tag);
            }
        }

        // 레디스 큐로 LPUSH
        if(titleChanged || contentChanged){
            embeddingProducer.sendEmbeddingRequest(
                    post.getId(),
                    titleChanged,   // title 수정 여부
                    contentChanged  // content 수정 여부
            );
        }

        return new BlogPostDetailDTO(post, categoryList, tags);
    }

    // 게시글 좋아요
    @Transactional
    @Override
    public boolean toggleLike(Long postId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            postLikeRepository.delete(existingLike.get());
            post.decrementLikeCount();
            return false; // 취소됨
        } else {
            // 좋아요 추가
            postLikeRepository.save(new PostLike(user, post));
            post.incrementLikeCount();
            return true; // 좋아요 됨
        }
    }

    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        post.incrementViewCount();
    }

    /** 내가 댓글 단 게시글 목록 조회 */
    @Transactional(readOnly = true)
    @Override
    public Page<BlogPostSummaryDTO> getMyCommentedPosts(UUID userId, Pageable pageable) {
        Page<BlogPost> commentedPosts = commentRepository.findCommentedPostsByUserId(userId, pageable);
        return commentedPosts.map(BlogPostSummaryDTO::new);
    }

    /** 내가 작성한 게시글 중 다른 사람의 댓글이 달린 게시글 목록 조회 */
    @Transactional(readOnly = true)
    @Override
    public Page<BlogPostSummaryDTO> getMyPostsWithComments(UUID userId, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByUserAndCommentExists(userId, pageable);
        return posts.map(BlogPostSummaryDTO::new);
    }

}
