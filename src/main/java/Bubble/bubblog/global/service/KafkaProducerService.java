package Bubble.bubblog.global.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 메시지를 지정된 토픽으로 보내는 메서드
    public void sendMessage(String topic, Object message) {
        log.info("Sending message to Kafka topic: {}, message: {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
