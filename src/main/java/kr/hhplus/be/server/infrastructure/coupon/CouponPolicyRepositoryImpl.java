package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CouponPolicyRepositoryImpl implements CouponPolicyRepository {

    private final CouponPolicyJpaRepository couponPolicyJpaRepository;

    public CouponPolicyRepositoryImpl(CouponPolicyJpaRepository couponPolicyJpaRepository) {
        this.couponPolicyJpaRepository = couponPolicyJpaRepository;
    }

    @Override
    public Optional<CouponPolicy> findById(int policyId) {
        return couponPolicyJpaRepository.findById(policyId)
            .map(CouponPolicyEntity::toDomain);
    }

    @Override
    public CouponPolicy save(CouponPolicy couponPolicy) {
        CouponPolicyEntity entity = CouponPolicyEntity.fromDomain(couponPolicy);
        CouponPolicyEntity savedEntity = couponPolicyJpaRepository.save(entity);
        return savedEntity.toDomain();
    }
} 