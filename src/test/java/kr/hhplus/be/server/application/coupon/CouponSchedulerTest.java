package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponSchedulerTest {

    @Mock
    private RedissonClient redissonClient;
    
    @Mock
    private CouponRepository couponRepository;
    
    @Mock
    private CouponPolicyRepository couponPolicyRepository;
    
    @Mock
    private RSet<Object> pendingSet;
    
    @InjectMocks
    private CouponScheduler couponScheduler;
    
    private CouponPolicy testPolicy;
    private Coupon testCoupon;
    
    @BeforeEach
    void setUp() {
        testPolicy = new CouponPolicy("테스트", 10, 30, 100, 
            java.time.LocalDateTime.now(), java.time.LocalDateTime.now().plusDays(30));
        testPolicy.setPolicyId(1);
        testPolicy.setIssuedCount(50);
        
        testCoupon = new Coupon(1, 1);
        testCoupon.setCouponId(1);
    }
    
    @Test
    void 쿠폰발급요청_동기화_성공() {
        // given
        Set<Object> pendingRequests = new HashSet<>();
        pendingRequests.add("1:1"); // policyId:userId
        
        when(redissonClient.getSet("coupon:issue-pending")).thenReturn(pendingSet);
        when(pendingSet.readAll()).thenReturn(pendingRequests);
        when(couponPolicyRepository.findById(1)).thenReturn(Optional.of(testPolicy));
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(testPolicy);
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);
        
        // when
        couponScheduler.syncCouponRequests();
        
        // then
        verify(couponPolicyRepository).findById(1);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
        verify(couponRepository).save(any(Coupon.class));
        verify(pendingSet).remove("1:1");
    }
    
    @Test
    void 쿠폰발급요청_동기화_재고부족() {
        // given
        Set<Object> pendingRequests = new HashSet<>();
        pendingRequests.add("1:1");
        
        testPolicy.setIssuedCount(100); // 재고 소진
        
        when(redissonClient.getSet("coupon:issue-pending")).thenReturn(pendingSet);
        when(pendingSet.readAll()).thenReturn(pendingRequests);
        when(couponPolicyRepository.findById(1)).thenReturn(Optional.of(testPolicy));
        
        // when
        couponScheduler.syncCouponRequests();
        
        // then
        verify(couponPolicyRepository).findById(1);
        verify(couponPolicyRepository, never()).save(any(CouponPolicy.class));
        verify(couponRepository, never()).save(any(Coupon.class));
        verify(pendingSet).remove("1:1");
    }
}
