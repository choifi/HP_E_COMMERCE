package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    public CouponRepositoryImpl(CouponJpaRepository couponJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = CouponEntity.fromDomain(coupon);
        CouponEntity savedEntity = couponJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Coupon> findById(int couponId) {
        return couponJpaRepository.findById(couponId)
            .map(CouponEntity::toDomain);
    }

    @Override
    public List<Coupon> findByUserId(int userId) {
        return couponJpaRepository.findByUserId(userId)
            .stream()
            .map(CouponEntity::toDomain)
            .collect(Collectors.toList());
    }
} 