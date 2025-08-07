package kr.hhplus.be.server.infrastructure.coupon;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_policy")
public class CouponPolicyEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private int policyId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "discount_rate", nullable = false)
    private int discountRate;
    
    @Column(name = "valid_days", nullable = false)
    private int validDays;
    
    @Column(name = "issued_count", nullable = false)
    private int issuedCount;
    
    @Column(name = "max_count", nullable = false)
    private int maxCount;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;
    
    public CouponPolicyEntity() {}
    
    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getDiscountRate() { return discountRate; }
    public void setDiscountRate(int discountRate) { this.discountRate = discountRate; }
    public int getValidDays() { return validDays; }
    public void setValidDays(int validDays) { this.validDays = validDays; }
    public int getIssuedCount() { return issuedCount; }
    public void setIssuedCount(int issuedCount) { this.issuedCount = issuedCount; }
    public int getMaxCount() { return maxCount; }
    public void setMaxCount(int maxCount) { this.maxCount = maxCount; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
    
    public kr.hhplus.be.server.domain.coupon.CouponPolicy toDomain() {
        kr.hhplus.be.server.domain.coupon.CouponPolicy policy = 
            new kr.hhplus.be.server.domain.coupon.CouponPolicy(name, discountRate, validDays, maxCount, startDate, endDate);
        
        policy.setPolicyId(policyId);
        policy.setIssuedCount(issuedCount);
        policy.setCreatedTime(createdTime);
        policy.setUpdatedTime(updatedTime);
        
        return policy;
    }
    
    public static CouponPolicyEntity fromDomain(kr.hhplus.be.server.domain.coupon.CouponPolicy policy) {
        CouponPolicyEntity entity = new CouponPolicyEntity();
        entity.setPolicyId(policy.getPolicyId());
        entity.setName(policy.getName());
        entity.setDiscountRate(policy.getDiscountRate());
        entity.setValidDays(policy.getValidDays());
        entity.setIssuedCount(policy.getIssuedCount());
        entity.setMaxCount(policy.getMaxCount());
        entity.setStartDate(policy.getStartDate());
        entity.setEndDate(policy.getEndDate());
        entity.setCreatedTime(policy.getCreatedTime());
        entity.setUpdatedTime(policy.getUpdatedTime());
        
        return entity;
    }
} 