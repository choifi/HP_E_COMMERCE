package kr.hhplus.be.server.domain.payment;

import java.util.Optional;

// 결제 조회 및 저장 기능 정의
public interface PaymentRepository {
    
    // 주문 ID로 결제 조회
    Optional<Payment> findByOrderId(int orderId);
    
    // 결제 업데이트
    Payment save(Payment payment);
} 