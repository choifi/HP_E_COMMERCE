package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(int couponId);
    List<Coupon> findByUserId(int userId);
    List<Coupon> findByPolicyId(int policyId);
} 