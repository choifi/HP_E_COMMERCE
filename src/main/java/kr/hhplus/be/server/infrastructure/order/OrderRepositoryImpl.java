package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntity.fromDomain(order);
        OrderEntity savedEntity = orderJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Order> findById(int orderId) {
        return orderJpaRepository.findById(orderId)
            .map(OrderEntity::toDomain);
    }

    @Override
    public List<Order> findByUserId(int userId) {
        return orderJpaRepository.findByUserIdOrderByCreatedTimeDesc(userId)
            .stream()
            .map(OrderEntity::toDomain)
            .collect(Collectors.toList());
    }
} 