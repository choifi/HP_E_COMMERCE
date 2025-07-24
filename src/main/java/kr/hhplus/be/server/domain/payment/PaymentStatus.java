package kr.hhplus.be.server.domain.payment;

// 결제 상태
public enum PaymentStatus {
    PENDING("대기"),
    COMPLETED("완료"),
    CANCELLED("취소"),
    FAILED("실패");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 