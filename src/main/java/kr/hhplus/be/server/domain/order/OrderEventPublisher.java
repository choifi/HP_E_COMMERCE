package kr.hhplus.be.server.domain.order;

public interface OrderEventPublisher {
    void publish(OrderCompletedEvent.Completed event);
}
