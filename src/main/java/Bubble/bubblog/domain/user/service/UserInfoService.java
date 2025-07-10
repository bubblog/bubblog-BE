package Bubble.bubblog.domain.user.service;

import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.UserPostsResponseDTO;
import Bubble.bubblog.domain.user.dto.infoRes.UserInfoDTO;
import Bubble.bubblog.domain.user.dto.req.UserUpdateDTO;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserInfoService {
    // 사용자 정보 서비스
    UserInfoDTO getUserInfo(UUID userId);
    void updateUser(UUID userId, UserUpdateDTO request);
    BlogPostDetailDTO getMyPost(Long postId, UUID userId);
    UserPostsResponseDTO getMyAllPosts(UUID userId, Long categoryId, Pageable pageable);
}