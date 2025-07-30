package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 주문 도메인 서비스와 관련된 비즈니스 규칙을 처리하는 도메인 서비스
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성 및 검증
    public Order createValidatedOrder(int userId, List<OrderItem> orderItems) {
        // 도메인 규칙 검증
        validateOrderItems(orderItems);
        
        // 주문 생성
        Order order = new Order(userId, orderItems);
        
        return order;
    }

    // 주문 저장
    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    // 주문 ID로 조회
    public Order findById(int orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + orderId));
    }

    // 사용자 ID로 주문 목록 조회
    public List<Order> findByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }

    // 주문 취소
    @Transactional
    public Order cancelOrder(int orderId) {
        Order order = findById(orderId);
        order.cancel();
        return orderRepository.save(order);
    }

    // 주문 상품 검증
    private void validateOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 없습니다.");
        }
        
        // 중복 상품 검증
        long uniqueProductCount = orderItems.stream()
            .mapToInt(OrderItem::getProductId)
            .distinct()
            .count();
            
        if (uniqueProductCount != orderItems.size()) {
            throw new IllegalArgumentException("중복된 상품이 있습니다.");
        }
    }

    // 할인 적용 검증
    public void validateDiscount(Order order, int discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 0 이상이어야 합니다.");
        }
        
        if (discountAmount > order.getTotalAmount()) {
            throw new IllegalArgumentException("할인 금액은 총 금액을 초과할 수 없습니다.");
        }
    }
} 