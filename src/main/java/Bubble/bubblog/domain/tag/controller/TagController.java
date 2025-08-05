package Bubble.bubblog.domain.tag.controller;

import Bubble.bubblog.domain.tag.dto.res.TagResponseDTO;
import Bubble.bubblog.domain.tag.service.TagService;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
@Tag(name = "Tag", description = "태그 관련 API")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "태그 목록 조회", description = "전체 태그 목록을 조회.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    @GetMapping
    public List<TagResponseDTO> getAllTags() {
        return tagService.getAllTags();
    }

    @Operation(summary = "태그 상세 조회", description = "특정 태그를 조회.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    @GetMapping("/{id}")
    public TagResponseDTO getTag(@PathVariable Long id) {
        return tagService.getTag(id);
    }

//    @Operation(summary = "태그 생성", description = "태그를 생성합니다.", security = @SecurityRequirement(name = "JWT"))
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "태그 생성 성공",
//                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
//            @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @PostMapping
//    public ResponseEntity<TagResponseDTO> createTag(@RequestBody @Valid TagRequestDTO request) {
//        TagResponseDTO response = tagService.createTag(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
}