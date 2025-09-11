package Bubble.bubblog.global.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    // 접속할 kafka 브로커의 주소 목록 (우리 프로젝트에서는 우선 하나)
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Kafka Producer 인스턴스를 생성하는 데 필요한 설정값들을 정의
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // Kafka 브로커의 주소를 설정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // 메시지의 key를 직렬화할 때 사용할 클래스를 지정
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 메시지의 value를 직렬화할 때 사용할 클래스를 지정. 여기서는 JSON 형태로 보낼 것이므로 JsonSerializer를 사용.
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // Kafka 메시지를 보내는 데 사용될 KafkaTemplate을 Bean으로 등록.
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}