package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

// 결제 도메인인
public class Payment {
    private int paymentId;
    private int orderId;
    private Integer couponId;  // 사용된 쿠폰 ID 
    private int totalAmount;   // 할인 전 금액
    private int discountAmount; // 할인 금액
    private int amount;        // 실제 결제 금액
    private PaymentStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Payment() {}

    public Payment(int orderId, int totalAmount, int discountAmount, Integer couponId) {
        validatePayment(orderId, totalAmount, discountAmount);
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.couponId = couponId;
        this.amount = totalAmount - discountAmount;
        this.status = PaymentStatus.PENDING;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    // 비즈니스 규칙 검증
    private void validatePayment(int orderId, int totalAmount, int discountAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("총 금액은 0 이상이어야 합니다.");
        }
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 0 이상이어야 합니다.");
        }
        if (discountAmount > totalAmount) {
            throw new IllegalArgumentException("할인 금액은 총 금액을 초과할 수 없습니다.");
        }
    }

    // 비즈니스 메서드: 결제 완료
    public void complete() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("대기 중인 결제만 완료할 수 있습니다.");
        }
        this.status = PaymentStatus.COMPLETED;
        this.updatedTime = LocalDateTime.now();
    }

    // 비즈니스 메서드: 결제 취소
    public void cancel() {
        if (this.status == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("완료된 결제는 취소할 수 없습니다.");
        }
        this.status = PaymentStatus.CANCELLED;
        this.updatedTime = LocalDateTime.now();
    }

    // JPA와 도메인 변환을 위한 메서드들
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    // Getter 메서드들
    public int getPaymentId() { return paymentId; }
    public int getOrderId() { return orderId; }
    public Integer getCouponId() { return couponId; }
    public int getTotalAmount() { return totalAmount; }
    public int getDiscountAmount() { return discountAmount; }
    public int getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
} 