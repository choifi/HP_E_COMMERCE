package kr.hhplus.be.server.infrastructure.dataplatform;

import kr.hhplus.be.server.domain.order.OrderCompletedEvent;
import kr.hhplus.be.server.infrastructure.external.DataPlatFormClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataOrderEventListener {

    private final DataPlatFormClient dataPlatFormClient;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void listen(OrderCompletedEvent.Completed event) {
        try {
            log.info("데이터 플랫폼 전송 시작: 주문 ID {}", event.orderId());
            
            dataPlatFormClient.sendOrder(event.orderId());
            
            log.info("데이터 플랫폼 전송 완료: 주문 ID {}", event.orderId());
            
        } catch (Exception e) {
            log.error("데이터 플랫폼 전송 실패: 주문 ID {}", event.orderId(), e);
        }
    }
}
