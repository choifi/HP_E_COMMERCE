package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.lock.DistributedLock;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    public CouponService(CouponRepository couponRepository, 
                        CouponPolicyRepository couponPolicyRepository) {
        this.couponRepository = couponRepository;
        this.couponPolicyRepository = couponPolicyRepository;
    }

    // 쿠폰 정책 조회
    @Cacheable(value = "coupon_policy", key = "#policyId")
    public CouponPolicy getCouponPolicyById(int policyId) {
        return couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책을 찾을 수 없습니다."));
    }

    @DistributedLock(
        key = "'coupon_policy:' + #policyId",
        waitTime = 10,
        leaseTime = 30,
        timeUnit = TimeUnit.SECONDS
    )
    @Transactional
    public Coupon issueCoupon(int userId, int policyId) {
        // 쿠폰 정책 조회 (비관적 락 적용)
        CouponPolicy policy = couponPolicyRepository.findByIdWithLock(policyId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책을 찾을 수 없습니다."));

        // 재고 확인
        if (policy.getIssuedCount() >= policy.getMaxCount()) {
            throw new IllegalStateException("쿠폰이 소진되었습니다.");
        }

        // 발급 카운트 증가
        policy.setIssuedCount(policy.getIssuedCount() + 1);
        couponPolicyRepository.save(policy);

        // 쿠폰 생성
        Coupon coupon = new Coupon(userId, policyId);
        return couponRepository.save(coupon);
    }

    public List<Coupon> getUserCoupons(int userId) {
        return couponRepository.findByUserId(userId);
    }

    public Coupon getCouponById(int couponId) {
        return couponRepository.findById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
    }

    @Transactional
    public void useCoupon(int couponId) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        coupon.use();
        couponRepository.save(coupon);
    }
} 