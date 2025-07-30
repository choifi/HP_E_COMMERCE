package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public Optional<Payment> findByOrderId(int orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
            .map(PaymentEntity::toDomain);
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.fromDomain(payment);
        PaymentEntity savedEntity = paymentJpaRepository.save(entity);
        return savedEntity.toDomain();
    }
} 