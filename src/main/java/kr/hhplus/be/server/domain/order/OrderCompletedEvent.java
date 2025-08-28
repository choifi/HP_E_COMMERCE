package kr.hhplus.be.server.domain.order;

public class OrderCompletedEvent {
    public record Completed(long orderId) {
    }
}