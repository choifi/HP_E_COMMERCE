package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

public class CouponPolicy {
    private int policyId;
    private String name;
    private int discountRate;
    private int validDays;
    private int issuedCount;
    private int maxCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int version;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public CouponPolicy() {}

    public CouponPolicy(String name, int discountRate, int validDays, int maxCount, 
                       LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.discountRate = discountRate;
        this.validDays = validDays;
        this.maxCount = maxCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issuedCount = 0;
        this.version = 0;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }
    public String getName() { return name; }
    public int getDiscountRate() { return discountRate; }
    public int getValidDays() { return validDays; }
    public int getIssuedCount() { return issuedCount; }
    public void setIssuedCount(int issuedCount) { this.issuedCount = issuedCount; }
    public int getMaxCount() { return maxCount; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
} 