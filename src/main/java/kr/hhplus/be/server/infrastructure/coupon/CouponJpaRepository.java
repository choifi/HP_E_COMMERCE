package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponJpaRepository extends JpaRepository<CouponEntity, Integer> {
    List<CouponEntity> findByUserId(int userId);
    List<CouponEntity> findByPolicyId(int policyId);
} 