package Bubble.bubblog.domain.chatbot.controller;

import Bubble.bubblog.domain.chatbot.dto.req.PersonaRequestDTO;
import Bubble.bubblog.domain.chatbot.dto.res.PersonaResponseDTO;
import Bubble.bubblog.domain.chatbot.service.PersonaService;
import Bubble.bubblog.global.dto.ErrorResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Persona", description = "챗봇 말투 커스터마이징")
@RestController
@RequestMapping(value = "/api/personas", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class PersonaController {

    private final PersonaService personaService;

    @Operation(summary = "말투 생성", description = "사용자가 챗봇의 말투를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "말투 생성 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    public SuccessResponse<PersonaResponseDTO> createPersona(
            @RequestBody @Valid PersonaRequestDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        PersonaResponseDTO p = personaService.createPersona(request, userId);
        return SuccessResponse.of(p);
    }

    @Operation(summary = "특정 말투 조회", description = "로그인한 사용자가 자신의 특정 말투를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "말투 조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 말투",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{personaId}")
    public SuccessResponse<PersonaResponseDTO> getPersona(
            @PathVariable Long personaId,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        PersonaResponseDTO dto = personaService.getPersonaById(personaId, userId);
        return SuccessResponse.of(dto);
    }

    @Operation(summary = "말투 목록 조회", description = "로그인한 사용자가 자신의 말투들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "말투 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public SuccessResponse<List<PersonaResponseDTO>> getMyPersonas(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        List<PersonaResponseDTO> personas = personaService.getPersonasByUserId(userId);
        return SuccessResponse.of(personas);
    }

    @Operation(summary = "말투 수정", description = "로그인한 사용자가 자신의 말투를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "말투 수정 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 말투",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{personaId}")
    public SuccessResponse<PersonaResponseDTO> updatePersona(
            @PathVariable Long personaId,
            @RequestBody @Valid PersonaRequestDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        PersonaResponseDTO updated = personaService.updatePersona(personaId, request, userId);
        return SuccessResponse.of(updated);
    }

    @Operation(summary = "말투 삭제", description = "로그인한 사용자가 자신의 말투를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "말투 삭제 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 말투",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{personaId}")
    public SuccessResponse<String> deletePersona(
            @PathVariable Long personaId,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        personaService.deletePersona(personaId, userId);
        return SuccessResponse.of();
    }
}
