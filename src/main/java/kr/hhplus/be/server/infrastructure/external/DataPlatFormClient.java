package kr.hhplus.be.server.infrastructure.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatFormClient {

    public void sendOrder(long orderId) {
        log.info("Mock API 호출: 주문 ID {} 데이터 전송", orderId);
    }
}
