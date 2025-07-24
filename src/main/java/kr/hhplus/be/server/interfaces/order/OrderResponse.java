package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 주문 응답 DTO
public class OrderResponse {

    private final int orderId;
    private final int userId;
    private final List<OrderItemResponse> orderItems;
    private final int totalAmount;
    private final int discountAmount;
    private final int amount;
    private final String status;
    private final LocalDateTime createdTime;
    private final LocalDateTime updatedTime;

    public OrderResponse(int orderId, int userId, List<OrderItemResponse> orderItems,
                        int totalAmount, int discountAmount, int amount,
                        String status, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.amount = amount;
        this.status = status;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // Domain 객체에서 Response DTO로 변환
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getOrderId(),
            order.getUserId(),
            orderItemResponses,
            order.getTotalAmount(),
            order.getDiscountAmount(),
            order.getAmount(),
            order.getStatus().name(),
            order.getCreatedTime(),
            order.getUpdatedTime()
        );
    }

    // Getter 
    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public List<OrderItemResponse> getOrderItems() { return orderItems; }
    public int getTotalAmount() { return totalAmount; }
    public int getDiscountAmount() { return discountAmount; }
    public int getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }

    // 주문 상품 응답 DTO
    public static class OrderItemResponse {
        private final int orderItemId;
        private final int productId;
        private final int quantity;
        private final int unitPrice;
        private final int totalPrice;
        private final LocalDateTime createdTime;
        private final LocalDateTime updatedTime;

        public OrderItemResponse(int orderItemId, int productId, int quantity, int unitPrice,
                               int totalPrice, LocalDateTime createdTime, LocalDateTime updatedTime) {
            this.orderItemId = orderItemId;
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.createdTime = createdTime;
            this.updatedTime = updatedTime;
        }

        public static OrderItemResponse from(OrderItem orderItem) {
            return new OrderItemResponse(
                orderItem.getOrderItemId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getTotalPrice(),
                orderItem.getCreatedTime(),
                orderItem.getUpdatedTime()
            );
        }

        // Getter
        public int getOrderItemId() { return orderItemId; }
        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public int getUnitPrice() { return unitPrice; }
        public int getTotalPrice() { return totalPrice; }
        public LocalDateTime getCreatedTime() { return createdTime; }
        public LocalDateTime getUpdatedTime() { return updatedTime; }
    }
} 