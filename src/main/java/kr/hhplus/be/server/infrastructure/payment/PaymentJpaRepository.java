package kr.hhplus.be.server.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Integer> {
    
    // 주문 ID로 결제 조회
    Optional<PaymentEntity> findByOrderId(int orderId);
} 