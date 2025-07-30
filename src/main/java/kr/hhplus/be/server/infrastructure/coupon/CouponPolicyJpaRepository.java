package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyEntity, Integer> {
    Optional<CouponPolicyEntity> findById(Integer policyId);
} 