package kr.hhplus.be.server.infrastructure.payment;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @Column(name = "order_id", nullable = false)
    private int orderId;

    @Column(name = "coupon_id")
    private Integer couponId;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private kr.hhplus.be.server.domain.payment.PaymentStatus status;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    public PaymentEntity() {}

    // Getter, Setter
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public Integer getCouponId() { return couponId; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }
    
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    
    public int getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }
    
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    public kr.hhplus.be.server.domain.payment.PaymentStatus getStatus() { return status; }
    public void setStatus(kr.hhplus.be.server.domain.payment.PaymentStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    // 도메인 변환 메서드
    public kr.hhplus.be.server.domain.payment.Payment toDomain() {
        kr.hhplus.be.server.domain.payment.Payment payment = 
            new kr.hhplus.be.server.domain.payment.Payment(orderId, totalAmount, discountAmount, couponId);
        
        payment.setPaymentId(paymentId);
        payment.setStatus(status);
        payment.setCreatedTime(createdTime);
        payment.setUpdatedTime(updatedTime);
        
        return payment;
    }

    // 도메인 객체에서 변환
    public static PaymentEntity fromDomain(kr.hhplus.be.server.domain.payment.Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.setPaymentId(payment.getPaymentId());
        entity.setOrderId(payment.getOrderId());
        entity.setCouponId(payment.getCouponId());
        entity.setTotalAmount(payment.getTotalAmount());
        entity.setDiscountAmount(payment.getDiscountAmount());
        entity.setAmount(payment.getAmount());
        entity.setStatus(payment.getStatus());
        entity.setCreatedTime(payment.getCreatedTime());
        entity.setUpdatedTime(payment.getUpdatedTime());
        
        return entity;
    }
} 