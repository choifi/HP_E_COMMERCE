package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final RedissonClient redissonClient;
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    
    private static final String COUPON_ISSUE_PENDING_KEY = "coupon:issue-pending";

    // 3초마다 Redis → DB 동기화 (STEP 14 배치처리)
    @Scheduled(fixedRate = 3000)
    @Transactional
    public void syncCouponRequests() {
        try {
            log.info("쿠폰 발급 요청 동기화 시작");
            
            // pending된 쿠폰 정책들 조회
            RSet<String> pendingSet = redissonClient.getSet(COUPON_ISSUE_PENDING_KEY);
            Set<String> pendingRequests = pendingSet.readAll();
            
            for (String requestKey : pendingRequests) {
                String[] parts = requestKey.split(":");
                if (parts.length == 2) {
                    int policyId = Integer.parseInt(parts[0]);
                    int userId = Integer.parseInt(parts[1]);
                    
                    processCouponRequest(policyId, userId);
                    
                    // 처리 완료된 요청 제거
                    pendingSet.remove(requestKey);
                }
            }
            
            log.info("쿠폰 발급 요청 동기화 완료");
        } catch (Exception e) {
            log.error("쿠폰 발급 요청 동기화 중 오류 발생", e);
        }
    }
    
    private void processCouponRequest(int policyId, int userId) {
        try {
            CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책을 찾을 수 없습니다."));
            
            // 재고 재확인
            if (policy.getIssuedCount() >= policy.getMaxCount()) {
                log.warn("쿠폰 재고 부족으로 발급 실패: policyId={}, userId={}", policyId, userId);
                return;
            }
            
            // 발급 카운트 증가
            policy.setIssuedCount(policy.getIssuedCount() + 1);
            couponPolicyRepository.save(policy);
            
            // 쿠폰 생성
            Coupon coupon = new Coupon(userId, policyId);
            couponRepository.save(coupon);
            
            log.info("쿠폰 발급 완료: policyId={}, userId={}, couponId={}", policyId, userId, coupon.getCouponId());
            
        } catch (Exception e) {
            log.error("쿠폰 발급 처리 중 오류: policyId={}, userId={}", policyId, userId, e);
        }
    }
}
