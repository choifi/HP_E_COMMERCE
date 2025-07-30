package kr.hhplus.be.server.domain.order;

/**
 * 주문 상태 열거형
 */
public enum OrderStatus {
    PENDING("대기 중"),
    COMPLETED("완료"),
    CANCEL_REQUESTED("취소 요청"),
    EXPIRED("만료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 