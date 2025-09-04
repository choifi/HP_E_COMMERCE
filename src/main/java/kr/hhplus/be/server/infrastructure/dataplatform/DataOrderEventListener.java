package kr.hhplus.be.server.infrastructure.dataplatform;

import kr.hhplus.be.server.application.data.DataPlatformService;
import kr.hhplus.be.server.domain.order.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class DataOrderEventListener {

    private final DataPlatformService dataPlatformService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void listen(OrderCompletedEvent.Completed event) {
        dataPlatformService.sendOrderData(event.orderId());
    }
}
