package kr.hhplus.be.server.interfaces.coupon;

public class IssueCouponRequest {
    private int userId;
    private int policyId;

    public IssueCouponRequest() {}

    public IssueCouponRequest(int userId, int policyId) {
        this.userId = userId;
        this.policyId = policyId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }
} 