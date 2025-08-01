package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

public class Coupon {
    private int couponId;
    private int userId;
    private int policyId;
    private CouponStatus status;
    private LocalDateTime createdTime;

    public Coupon(int userId, int policyId) {
        this.userId = userId;
        this.policyId = policyId;
        this.status = CouponStatus.ISSUED;
        this.createdTime = LocalDateTime.now();
    }

    public void use() {
        if (status != CouponStatus.ISSUED) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }
        this.status = CouponStatus.USED;
    }

    public int getCouponId() { return couponId; }
    public void setCouponId(int couponId) { this.couponId = couponId; }
    public int getUserId() { return userId; }
    public int getPolicyId() { return policyId; }
    public CouponStatus getStatus() { return status; }
    public void setStatus(CouponStatus status) { this.status = status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
} 