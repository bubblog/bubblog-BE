package Bubble.bubblog.domain.user.controller;

import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.domain.user.dto.infoRes.UserInfoDTO;
import Bubble.bubblog.domain.user.dto.req.UserUpdateDTO;
import Bubble.bubblog.domain.user.service.UserService;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User Info", description = "특정 사용자 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users", produces = "application/json")
@SecurityRequirement(name = "JWT")
public class UserInfoController {

    private final UserService userService;

    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 공개 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{userId}")
    public SuccessResponse<UserInfoDTO> getUserInfo(
            @PathVariable UUID userId,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId) {
        return SuccessResponse.of(userService.getUserInfo(userId));
    }

    @Operation(summary = "사용자 정보 수정", description = "현재 로그인된 유저의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 혹은 닉네임 중복",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PatchMapping("/me")
    public SuccessResponse<Void> updateUser(
            @Valid @RequestBody UserUpdateDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        userService.updateUser(userId, request);
        return SuccessResponse.of();
    }

    // 내가 좋아요 누른 게시글 조회
    @Operation(summary = "좋아요 누른 게시글 목록 조회", description = "사용자가 좋아요를 누른 게시글 목록을 페이지 단위로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/me/likes")
    public SuccessResponse<Page<BlogPostSummaryDTO>> getLikedPosts(
            @AuthenticationPrincipal UUID userId,
            @ParameterObject @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return SuccessResponse.of(userService.getLikedPosts(userId, pageable));
    }


}
