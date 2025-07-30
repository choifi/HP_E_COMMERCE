package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PointService pointService;
    private final CouponService couponService;
    private final CouponPolicyRepository couponPolicyRepository;

    public PaymentService(PaymentRepository paymentRepository, PointService pointService, 
                         CouponService couponService, CouponPolicyRepository couponPolicyRepository) {
        this.paymentRepository = paymentRepository;
        this.pointService = pointService;
        this.couponService = couponService;
        this.couponPolicyRepository = couponPolicyRepository;
    }

    //쿠폰 할인 금액 계산
    public int calculateDiscount(int totalAmount, int couponId) {
        // 쿠폰 조회
        Coupon coupon = couponService.getCouponById(couponId);
        
        // 쿠폰 정책 조회
        CouponPolicy policy = couponPolicyRepository.findById(coupon.getPolicyId())
            .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책을 찾을 수 없습니다."));
        
        // 할인율 적용
        return (int) (totalAmount * (policy.getDiscountRate() / 100.0));
    }

    // 사용자 포인트 차감
    @Transactional
    public void deductUserPoints(int userId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감할 금액은 0보다 커야 합니다.");
        }
    
        pointService.usePoint(userId, amount);
    }

    // 결제
    @Transactional
    public Payment processPayment(Order order, Integer couponId) {
        if (order.getAmount() <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }

        // 쿠폰 사용 처리
        if (couponId != null) {
            couponService.useCoupon(couponId);
        }

        // 결제 정보 생성
        Payment payment = new Payment(
            order.getOrderId(),
            order.getTotalAmount(),
            order.getDiscountAmount(),
            couponId
        );

        // 결제 완료
        payment.complete();

        // 결제 저장
        return paymentRepository.save(payment);
    }

    // 주문 ID로 결제 조회
    public Payment getPaymentByOrderId(int orderId) {

        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. 주문 ID: " + orderId));
    }
} 