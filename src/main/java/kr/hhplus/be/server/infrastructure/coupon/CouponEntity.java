package kr.hhplus.be.server.infrastructure.coupon;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
public class CouponEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private int couponId;
    
    @Column(name = "user_id", nullable = false)
    private int userId;
    
    @Column(name = "policy_id", nullable = false)
    private int policyId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private kr.hhplus.be.server.domain.coupon.CouponStatus status;
    
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    public CouponEntity() {}
    
    public int getCouponId() { return couponId; }
    public void setCouponId(int couponId) { this.couponId = couponId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }
    public kr.hhplus.be.server.domain.coupon.CouponStatus getStatus() { return status; }
    public void setStatus(kr.hhplus.be.server.domain.coupon.CouponStatus status) { this.status = status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public kr.hhplus.be.server.domain.coupon.Coupon toDomain() {
        kr.hhplus.be.server.domain.coupon.Coupon coupon = new kr.hhplus.be.server.domain.coupon.Coupon(userId, policyId);
        coupon.setCouponId(couponId);
        coupon.setStatus(status);
        coupon.setCreatedTime(createdTime);
        return coupon;
    }
    
    public static CouponEntity fromDomain(kr.hhplus.be.server.domain.coupon.Coupon coupon) {
        CouponEntity entity = new CouponEntity();
        entity.setCouponId(coupon.getCouponId());
        entity.setUserId(coupon.getUserId());
        entity.setPolicyId(coupon.getPolicyId());
        entity.setStatus(coupon.getStatus());
        entity.setCreatedTime(coupon.getCreatedTime());
        return entity;
    }
} 