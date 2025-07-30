package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PointService pointService;
    @Mock
    private CouponService couponService;
    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, pointService, couponService, couponPolicyRepository);
    }

    @Test
    void calculateDiscount_성공() {
        int totalAmount = 1000;
        int couponId = 1;
        int policyId = 1;
        int discountRate = 10;

        Coupon coupon = new Coupon(1, policyId);
        coupon.setCouponId(couponId);
        
        CouponPolicy policy = new CouponPolicy("테스트 쿠폰", discountRate, 7, 100, null, null);
        policy.setPolicyId(policyId);

        when(couponService.getCouponById(couponId)).thenReturn(coupon);
        when(couponPolicyRepository.findById(policyId)).thenReturn(java.util.Optional.of(policy));

        int result = paymentService.calculateDiscount(totalAmount, couponId);

        assertThat(result).isEqualTo(100); // 1000 * 10% = 100
        verify(couponService).getCouponById(couponId);
        verify(couponPolicyRepository).findById(policyId);
    }

    @Test
    void processPayment_쿠폰사용_성공() {
        int orderId = 1;
        int couponId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 500); // productId=1, quantity=2, unitPrice=500
        Order order = new Order(1, java.util.List.of(orderItem));
        order.setOrderId(orderId);

        Payment payment = new Payment(orderId, 1000, 100, couponId);
        payment.setPaymentId(1);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.processPayment(order, couponId);

        assertThat(result).isNotNull();
        verify(couponService).useCoupon(couponId);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processPayment_쿠폰없음_성공() {
        int orderId = 1;
        OrderItem orderItem = new OrderItem(1, 1, 1000); // productId=1, quantity=1, unitPrice=1000
        Order order = new Order(1, java.util.List.of(orderItem));
        order.setOrderId(orderId);

        Payment payment = new Payment(orderId, 1000, 0, null);
        payment.setPaymentId(1);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.processPayment(order, null);

        assertThat(result).isNotNull();
        verify(couponService, never()).useCoupon(anyInt());
        verify(paymentRepository).save(any(Payment.class));
    }
} 