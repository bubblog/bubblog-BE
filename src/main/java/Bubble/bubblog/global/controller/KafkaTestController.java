package Bubble.bubblog.global.controller;

import Bubble.bubblog.global.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/** 테스트 용으로 만든 컨트롤러 */

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaTestController {
    private final KafkaProducerService producerService;

    // 테스트를 위해 임시로 만든 API
    @PostMapping("/publish")
    public String sendMessage(@RequestParam("message") String message) {

        // 실제로는 DTO 객체를 만들어서 보내는 것이 좋습니다.
        // 테스트를 위해 간단히 Map을 사용합니다.
        Map<String, String> messageObject = new HashMap<>();
        messageObject.put("content", message);

        // "bubblog-topic" 이라는 토픽으로 메시지를 보냅니다.
        producerService.sendMessage("bubblog-topic", messageObject);

        return "Message sent to Kafka topic: bubblog-topic";
    }
}
