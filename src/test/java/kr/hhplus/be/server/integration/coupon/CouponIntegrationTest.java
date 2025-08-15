package kr.hhplus.be.server.integration.coupon;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class CouponIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;
    
    @Autowired
    private CouponRepository couponRepository;

    private CouponPolicy testPolicy;
    private Coupon testCoupon;

    @BeforeEach
    @Transactional
    void setUp() {
        // 테스트용 쿠폰 정책 생성
        testPolicy = new CouponPolicy(
            "테스트 할인", 
            20, 
            30, 
            1000, 
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(30)
        );
        testPolicy = couponPolicyRepository.save(testPolicy);
        
        // 쿠폰 발급
        testCoupon = couponService.issueCoupon(1, testPolicy.getPolicyId());
    }

    @Test
    @Transactional
    void 쿠폰_정책_생성_성공() {
        // given
        CouponPolicy policy = new CouponPolicy(
            "30% 할인", 
            30, 
            60, 
            500, 
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(60)
        );

        // when
        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        // then
        assertThat(savedPolicy.getPolicyId()).isGreaterThan(0);
        assertThat(savedPolicy.getName()).isEqualTo("30% 할인");
        assertThat(savedPolicy.getDiscountRate()).isEqualTo(30);
        assertThat(savedPolicy.getValidDays()).isEqualTo(60);
        assertThat(savedPolicy.getMaxCount()).isEqualTo(500);
    }

    @Test
    @Transactional
    void 쿠폰_정책_조회_성공() {
        // when
        CouponPolicy foundPolicy = couponPolicyRepository.findById(testPolicy.getPolicyId()).orElse(null);

        // then
        assertThat(foundPolicy).isNotNull();
        assertThat(foundPolicy.getPolicyId()).isEqualTo(testPolicy.getPolicyId());
        assertThat(foundPolicy.getName()).isEqualTo("테스트 할인");
        assertThat(foundPolicy.getDiscountRate()).isEqualTo(20);
    }


    @Test
    @Transactional
    void 쿠폰_발급_성공() {
        // given
        CouponPolicy policy = new CouponPolicy(
            "15% 할인", 
            15, 
            45, 
            200, 
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(45)
        );
        policy = couponPolicyRepository.save(policy);

        // when
        Coupon coupon = couponService.issueCoupon(2, policy.getPolicyId());

        // then
        assertThat(coupon.getCouponId()).isGreaterThan(0);
        assertThat(coupon.getUserId()).isEqualTo(2);
        assertThat(coupon.getPolicyId()).isEqualTo(policy.getPolicyId());
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ISSUED);
    }

    @Test
    @Transactional
    void 쿠폰_조회_성공() {
        // when
        Coupon foundCoupon = couponService.getCouponById(testCoupon.getCouponId());

        // then
        assertThat(foundCoupon.getCouponId()).isEqualTo(testCoupon.getCouponId());
        assertThat(foundCoupon.getUserId()).isEqualTo(1);
        assertThat(foundCoupon.getPolicyId()).isEqualTo(testPolicy.getPolicyId());
        assertThat(foundCoupon.getStatus()).isEqualTo(CouponStatus.ISSUED);
    }

    @Test
    @Transactional
    void 쿠폰_조회_실패_존재하지_않는_쿠폰() {
        // when & then
        assertThatThrownBy(() -> couponService.getCouponById(999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @Test
    @Transactional
    void 사용자_쿠폰_목록_조회_성공() {
        // given
        CouponPolicy policy2 = new CouponPolicy(
            "25% 할인", 
            25, 
            30, 
            100, 
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(30)
        );
        policy2 = couponPolicyRepository.save(policy2);
        
        // 같은 사용자에게 쿠폰 추가 발급
        couponService.issueCoupon(1, policy2.getPolicyId());

        // when
        List<Coupon> userCoupons = couponService.getUserCoupons(1);

        // then
        assertThat(userCoupons).hasSize(2);
        assertThat(userCoupons).allMatch(coupon -> coupon.getUserId() == 1);
    }

    @Test
    @Transactional
    void 쿠폰_사용_성공() {
        // when
        couponService.useCoupon(testCoupon.getCouponId());

        // then
        Coupon usedCoupon = couponService.getCouponById(testCoupon.getCouponId());
        assertThat(usedCoupon.getStatus()).isEqualTo(CouponStatus.USED);
    }

    @Test
    @Transactional
    void 쿠폰_사용_실패_존재하지_않는_쿠폰() {
        // when & then
        assertThatThrownBy(() -> couponService.useCoupon(999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @Test
    @Transactional
    void 쿠폰_사용_실패_이미_사용된_쿠폰() {
        // given
        couponService.useCoupon(testCoupon.getCouponId());

        // when & then
        assertThatThrownBy(() -> couponService.useCoupon(testCoupon.getCouponId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("사용할 수 없는 쿠폰입니다");
    }

    @Test
    @Transactional
    void 쿠폰_할인_계산_성공() {
        // given
        int totalAmount = 50000;
        int discountRate = testPolicy.getDiscountRate();

        // when
        int discountAmount = (int) (totalAmount * (discountRate / 100.0));

        // then
        assertThat(discountAmount).isEqualTo(10000);
    }

    @Test
    void 선착순_쿠폰_동시성_테스트() throws InterruptedException {
        // given - 쿠폰 5개 발급 가능
        final CouponPolicy policy = new CouponPolicy("테스트", 10, 30, 5, 
            LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        final CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        // when - 10명이 동시에 신청
        int count = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(count);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(count);

        for (int i = 0; i < count; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    couponService.issueCoupon(userId, savedPolicy.getPolicyId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        // then - 비관적 락으로 정확히 5개만 발급됨
        List<Coupon> issuedCoupons = couponRepository.findByPolicyId(savedPolicy.getPolicyId());
        
        System.out.println("발급되어야 할 쿠폰 수: " + savedPolicy.getMaxCount());
        System.out.println("실제 발급된 쿠폰: " + issuedCoupons.size());
        
        assertThat(issuedCoupons.size()).isEqualTo(5);
    }

} 