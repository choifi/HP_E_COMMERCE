package kr.hhplus.be.server.domain.order;

import java.util.List;

// 주문 도메인 서비스

public class OrderDomainService {

    // 주문 생성 및 검증
    public static Order createValidatedOrder(int userId, List<OrderItem> orderItems) {
        // 도메인 규칙 검증
        validateOrderItems(orderItems);
        
        // 주문 생성
        Order order = new Order(userId, orderItems);
        
        return order;
    }

    // 주문 상품 검증
    private static void validateOrderItems(List<OrderItem> orderItems) {
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
    public static void validateDiscount(Order order, int discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 0 이상이어야 합니다.");
        }
        
        if (discountAmount > order.getTotalAmount()) {
            throw new IllegalArgumentException("할인 금액은 총 금액을 초과할 수 없습니다.");
        }
    }
} 