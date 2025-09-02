package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderCompletedEvent;
import kr.hhplus.be.server.domain.order.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApplicationEventPublisher implements OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(OrderCompletedEvent.Completed event) {
        applicationEventPublisher.publishEvent(event);
    }
}