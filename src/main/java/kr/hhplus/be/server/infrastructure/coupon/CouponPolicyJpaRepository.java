package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyEntity, Integer> {
    Optional<CouponPolicyEntity> findById(Integer policyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cp FROM CouponPolicyEntity cp WHERE cp.policyId = :policyId")
    Optional<CouponPolicyEntity> findByIdWithLock(Integer policyId);
} 