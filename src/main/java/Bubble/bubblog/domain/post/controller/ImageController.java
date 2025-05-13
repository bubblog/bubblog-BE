package Bubble.bubblog.domain.post.controller;

import Bubble.bubblog.domain.post.dto.req.S3UploadRequestDTO;
import Bubble.bubblog.domain.post.dto.res.PresignedUrlDTO;
import Bubble.bubblog.global.dto.ErrorResponse;
import Bubble.bubblog.global.dto.SuccessResponse;
import Bubble.bubblog.global.service.S3Service;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Post Images", description = "이미지 관련 API")
@RestController
@RequestMapping(value = "/api/uploads", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class ImageController {

    private final S3Service s3Service;

    // presigend url을 반환받는 컨트롤러
    @Operation(summary = "Presigned Url 발급", description = "S3에 업로드할 수 있는 presigned URL을 발급하고 객체(이미지) URL도 생성해 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned-URL 발급 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "파일 이름이나 타입이 잘못된 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/presigned-url")
    public SuccessResponse<PresignedUrlDTO> getPresignedUrl(@Valid @RequestParam S3UploadRequestDTO request,
                                                            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        return SuccessResponse.of(s3Service.generatePresignedUrl(request.getFileName(), request.getContentType()));
    }
}

