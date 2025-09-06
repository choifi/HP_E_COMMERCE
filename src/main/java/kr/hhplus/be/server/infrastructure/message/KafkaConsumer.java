package kr.hhplus.be.server.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    
    private final ObjectMapper objectMapper;
    
    @KafkaListener(topics = "order-completed", groupId = "data-platform")
    public void consumeOrderCompleted(String message) {
        try {
            OrderDataMessage orderData = objectMapper.readValue(message, OrderDataMessage.class);
        } catch (Exception e) {
        }
    }
}
