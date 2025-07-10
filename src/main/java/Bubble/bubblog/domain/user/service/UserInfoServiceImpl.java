package Bubble.bubblog.domain.user.service;

import Bubble.bubblog.domain.category.repository.CategoryClosureRepository;
import Bubble.bubblog.domain.category.repository.CategoryRepository;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.domain.post.dto.res.UserPostsResponseDTO;
import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.post.repository.BlogPostRepository;
import Bubble.bubblog.domain.post.repository.PostLikeRepository;
import Bubble.bubblog.domain.user.dto.infoRes.UserInfoDTO;
import Bubble.bubblog.domain.user.dto.req.UserUpdateDTO;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final CategoryClosureRepository categoryClosureRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;

    // user 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserInfoDTO getUserInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserInfoDTO(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }

    // user 정보 수정
    @Override
    @Transactional
    public void updateUser(UUID userId, UserUpdateDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 수정
        String newNickname = request.getNickname();
        if (newNickname != null && !newNickname.equals(user.getNickname())) {
            if (userRepository.existsByNickname(newNickname)) {
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            }
            user.updateNickname(newNickname);
        }

        // 프로필 이미지 수정 (null이면 이미지 제거)
        user.updateProfileImageUrl(request.getProfileImageUrl());
    }

    // 나의 게시글 상세 조회 (공개 및 비공개 포함)
    @Override
    @Transactional(readOnly = true)
    public BlogPostDetailDTO getMyPost(Long postId, UUID userId) {
       BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 로그인한 사용자가 게시글의 소유자인지 확인
        boolean isOwner = post.getUser().getId().equals(userId);

        if (!isOwner) {
            // 로그인했지만, 자신의 게시글이 아니라면 접근 거부 (비공개든 공개든 상관없이)
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        // 소유자라면 공개/비공개 여부와 상관없이 접근 허용
        List<String> categoryList = categoryClosureRepository.findAncestorNamesByDescendantId(post.getCategory().getId());
        return new BlogPostDetailDTO(post, categoryList);
    }

    // 나의 게시글 목록 조회 (공개 및 비공개 포함)
    @Override
    @Transactional(readOnly = true)
    public UserPostsResponseDTO getMyAllPosts(UUID userId, Long categoryId, Pageable pageable) {
        List<Long> categoryIds = null;
        if (categoryId != null) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            categoryIds = categoryClosureRepository.findAllSubtreeIdsIncludingSelf(categoryId);
        }

        Page<BlogPost> posts = blogPostRepository
                .searchUserPosts(userId, true, categoryIds, pageable);   // isOwner을 false로 고정 -> 공개 게시글만 조회하니까

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserPostsResponseDTO(
                user.getId(),
                user.getNickname(),
                posts.map(BlogPostSummaryDTO::new).getContent()
        );
    }

}
