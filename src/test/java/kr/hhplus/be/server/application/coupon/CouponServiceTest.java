package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponService = new CouponService(couponRepository);
    }

    @Test
    void issueCoupon_성공() {
        int userId = 1;
        int policyId = 1;
        Coupon coupon = new Coupon(userId, policyId);
        coupon.setCouponId(1);

        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon result = couponService.issueCoupon(userId, policyId);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getPolicyId()).isEqualTo(policyId);
        assertThat(result.getStatus()).isEqualTo(CouponStatus.ISSUED);

        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void getUserCoupons_성공() {
        int userId = 1;
        Coupon coupon1 = new Coupon(userId, 1);
        Coupon coupon2 = new Coupon(userId, 2);
        List<Coupon> coupons = List.of(coupon1, coupon2);

        when(couponRepository.findByUserId(userId)).thenReturn(coupons);

        List<Coupon> result = couponService.getUserCoupons(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(1).getUserId()).isEqualTo(userId);

        verify(couponRepository).findByUserId(userId);
    }

    @Test
    void useCoupon_성공() {
        int couponId = 1;
        Coupon coupon = new Coupon(1, 1);
        coupon.setCouponId(couponId);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        couponService.useCoupon(couponId);

        verify(couponRepository).findById(couponId);
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void useCoupon_존재하지않음_실패() {
        int couponId = 999;

        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.useCoupon(couponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("쿠폰을 찾을 수 없습니다");
    }
} 