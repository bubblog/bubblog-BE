package Bubble.bubblog.global.service;

import Bubble.bubblog.global.exception.dto.AiEmbeddingRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiService {

    private final WebClient webClient;

    public AiService(@Value("${ai.server.host}") String host,
                     @Value("${ai.server.port}") String port,
                     WebClient.Builder webClientBuilder) {
        String baseUrl = host + ":" + port;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build(); // AI 서버 주소
    }

    public void handlePostCreatedOrUpdated(Long postId, String content) {
        AiEmbeddingRequestDTO request = new AiEmbeddingRequestDTO(postId, content);
        webClient.post()
                .uri("/embed")   // AI 서버 경로
                .bodyValue(request)
                .retrieve()   // 응답 수신 준비
                .bodyToMono(Void.class)   // 현재는 응답 본문이 없다고 명시, 나중에 String.class나 DTO.class로 수정 가능
                .subscribe();  // 비동기 호출 - 응답을 기다리지 않고 요청만 보냄. 이 코드에 의해 임베딩 실패와 별개로 게시글 생성이나 수정 작업은 계속 진행된다.
    }

    public void handlePostDeleted(Long postId) {
        webClient.delete()
                .uri("/embed/" + postId)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}
