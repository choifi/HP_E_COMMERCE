package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;
    
    @Mock
    private CouponPolicyRepository couponPolicyRepository;
    
    @Mock
    private RedissonClient redissonClient;
    
    @Mock
    private RAtomicLong stockAtomicLong;
    
    @Mock
    private RScoredSortedSet<Object> issueSet;
    
    @Mock
    private RSet<Object> issuedUsers;
    
    @Mock
    private RSet<Object> pendingSet;
    
    @InjectMocks
    private CouponService couponService;
    
    private CouponPolicy testPolicy;
    
    @BeforeEach
    void setUp() {
        testPolicy = new CouponPolicy("테스트", 10, 30, 100, 
            java.time.LocalDateTime.now(), java.time.LocalDateTime.now().plusDays(30));
        testPolicy.setPolicyId(1);
        testPolicy.setIssuedCount(50);
    }
    
    @Test
    void 쿠폰발급요청_성공() {
        // given
        int userId = 1;
        int policyId = 1;
        
        when(couponPolicyRepository.findById(policyId))
            .thenReturn(Optional.of(testPolicy));
        when(redissonClient.getAtomicLong("coupon:stock:1"))
            .thenReturn(stockAtomicLong);
        when(stockAtomicLong.get()).thenReturn(50L);
        when(redissonClient.getSet("coupon:issued-users:1"))
            .thenReturn(issuedUsers);
        when(issuedUsers.add(String.valueOf(userId))).thenReturn(true);
        when(redissonClient.getScoredSortedSet("coupon:issue:1"))
            .thenReturn(issueSet);
        when(issueSet.rank(String.valueOf(userId))).thenReturn(0);
        when(redissonClient.getSet("coupon:issue-pending"))
            .thenReturn(pendingSet);
        
        // when
        CouponService.CouponIssueResult result = couponService.requestCoupon(userId, policyId);
        
        // then
        assertEquals(CouponService.CouponIssueResult.SUCCESS, result);
        verify(pendingSet).add("1:1");
    }
    
    @Test
    void 쿠폰발급요청_재고부족() {
        // given
        int userId = 1;
        int policyId = 1;
        
        when(couponPolicyRepository.findById(policyId))
            .thenReturn(Optional.of(testPolicy));
        when(redissonClient.getAtomicLong("coupon:stock:1"))
            .thenReturn(stockAtomicLong);
        when(stockAtomicLong.get()).thenReturn(0L);
        
        // when
        CouponService.CouponIssueResult result = couponService.requestCoupon(userId, policyId);
        
        // then
        assertEquals(CouponService.CouponIssueResult.SOLD_OUT, result);
    }
    
    @Test
    void issueCoupon_성공() {
        int userId = 1;
        int policyId = 1;
        Coupon coupon = new Coupon(userId, policyId);
        coupon.setCouponId(1);
        
        CouponPolicy policy = new CouponPolicy("테스트", 10, 30, 100, 
            java.time.LocalDateTime.now(), java.time.LocalDateTime.now().plusDays(30));
        policy.setIssuedCount(0);

        when(couponPolicyRepository.findById(policyId)).thenReturn(Optional.of(policy));
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(policy);
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon result = couponService.issueCoupon(userId, policyId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(policyId, result.getPolicyId());
    }
} 