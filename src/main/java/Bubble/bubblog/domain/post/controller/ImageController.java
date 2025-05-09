package Bubble.bubblog.domain.post.controller;

import Bubble.bubblog.domain.post.dto.res.PresignedUrlDTO;
import Bubble.bubblog.global.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post Images", description = "이미지 관련 API")
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class ImageController {

    private final S3Service s3Service;

    // presigend url을 반환받는 컨트롤러
    @Operation(summary = "Presigned Url 발급")
    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlDTO> getPresignedUrl(@RequestParam String fileName, @RequestParam String contentType) {
        return ResponseEntity.ok(s3Service.generatePresignedUrl(fileName, contentType));
    }
}

