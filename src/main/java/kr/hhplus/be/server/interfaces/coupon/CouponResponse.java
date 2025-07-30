package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;

public class CouponResponse {
    private int couponId;
    private int userId;
    private int policyId;
    private String status;

    public CouponResponse() {}

    public CouponResponse(int couponId, int userId, int policyId, String status) {
        this.couponId = couponId;
        this.userId = userId;
        this.policyId = policyId;
        this.status = status;
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
            coupon.getCouponId(),
            coupon.getUserId(),
            coupon.getPolicyId(),
            coupon.getStatus().name()
        );
    }

    public int getCouponId() { return couponId; }
    public int getUserId() { return userId; }
    public int getPolicyId() { return policyId; }
    public String getStatus() { return status; }
} 