package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

public interface CouponPolicyRepository {
    Optional<CouponPolicy> findById(int policyId);
    CouponPolicy save(CouponPolicy couponPolicy);
} 