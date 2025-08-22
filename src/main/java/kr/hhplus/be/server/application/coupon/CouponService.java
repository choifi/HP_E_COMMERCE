package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.lock.DistributedLock;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final RedissonClient redissonClient;
    
    // 키 구조
    private static final String COUPON_STOCK_KEY = "coupon:stock:{policyId}";
    private static final String COUPON_ISSUE_KEY = "coupon:issue:{policyId}";
    private static final String COUPON_ISSUE_PENDING_KEY = "coupon:issue-pending";
    private static final String COUPON_ISSUED_USERS_KEY = "coupon:issued-users:{policyId}";

    public CouponService(CouponRepository couponRepository, 
                        CouponPolicyRepository couponPolicyRepository,
                        RedissonClient redissonClient) {
        this.couponRepository = couponRepository;
        this.couponPolicyRepository = couponPolicyRepository;
        this.redissonClient = redissonClient;
    }

    // 쿠폰 정책 조회
    @Cacheable(value = "coupon_policy", key = "#policyId")
    public CouponPolicy getCouponPolicyById(int policyId) {
        return couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책을 찾을 수 없습니다."));
    }

    public CouponIssueResult requestCoupon(int userId, int policyId) {
        CouponPolicy policy = getCouponPolicyById(policyId);
        
        // 1. 재고 확인
        String stockKey = COUPON_STOCK_KEY.replace("{policyId}", String.valueOf(policyId));
        long availableStock = redissonClient.getAtomicLong(stockKey).get();
        
        if (availableStock <= 0) {
            return CouponIssueResult.SOLD_OUT;
        }
        
        // 2. 중복 발급 방지
        String issuedUsersKey = COUPON_ISSUED_USERS_KEY.replace("{policyId}", String.valueOf(policyId));
        RSet<String> issuedUsers = redissonClient.getSet(issuedUsersKey);
        if (!issuedUsers.add(String.valueOf(userId))) {
            return CouponIssueResult.ALREADY_ISSUED;
        }
        
        // 3. 선착순 순서 보장
        String issueKey = COUPON_ISSUE_KEY.replace("{policyId}", String.valueOf(policyId));
        RScoredSortedSet<String> issueSet = redissonClient.getScoredSortedSet(issueKey);
        issueSet.add(System.currentTimeMillis(), String.valueOf(userId));
        
        // 4. 순위 확인 및 재고 할당
        int rank = issueSet.rank(String.valueOf(userId));
        
        if (rank < availableStock) {
            // 발급 성공
            RSet<String> pendingSet = redissonClient.getSet(COUPON_ISSUE_PENDING_KEY);
            pendingSet.add(policyId + ":" + userId);
            
            // TTL
            issuedUsers.expire(24, TimeUnit.HOURS);
            issueSet.expire(24, TimeUnit.HOURS);
            
            return CouponIssueResult.SUCCESS;
        } else {
            // 재고 부족
            issuedUsers.remove(String.valueOf(userId));
            issueSet.remove(String.valueOf(userId));
            return CouponIssueResult.SOLD_OUT;
        }
    }

    @DistributedLock(
        key = "'coupon_policy:' + #policyId",
        waitTime = 10,
        leaseTime = 30,
        timeUnit = TimeUnit.SECONDS
    )
    @Transactional
    public Coupon issueCoupon(int userId, int policyId) {
        // 쿠폰 정책 조회 (캐시)
        CouponPolicy policy = getCouponPolicyById(policyId);
        
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
        Coupon coupon = getCouponById(couponId);
        coupon.use();
        couponRepository.save(coupon);
    }
    

    public void initializeCouponStock(int policyId, int maxCount) {
        String stockKey = COUPON_STOCK_KEY.replace("{policyId}", String.valueOf(policyId));
        redissonClient.getAtomicLong(stockKey).set(maxCount);
        

        String issueKey = COUPON_ISSUE_KEY.replace("{policyId}", String.valueOf(policyId));
        redissonClient.getScoredSortedSet(issueKey).clear();
        
        String issuedUsersKey = COUPON_ISSUED_USERS_KEY.replace("{policyId}", String.valueOf(policyId));
        redissonClient.getSet(issuedUsersKey).clear();
    }
    
    public enum CouponIssueResult {
        SUCCESS, SOLD_OUT, ALREADY_ISSUED
    }
} 