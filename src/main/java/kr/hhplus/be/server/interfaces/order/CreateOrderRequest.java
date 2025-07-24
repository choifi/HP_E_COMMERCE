package kr.hhplus.be.server.interfaces.order;

import java.util.List;

/**
 * 주문 생성 요청 DTO
 * 외부에서 주문 생성 시 전달받는 데이터 형식 정의
 */
public class CreateOrderRequest {

    private int userId;
    private List<OrderItemRequest> orderItems;
    private Integer couponId; // 선택사항

    public CreateOrderRequest() {}

    public CreateOrderRequest(int userId, List<OrderItemRequest> orderItems, Integer couponId) {
        this.userId = userId;
        this.orderItems = orderItems;
        this.couponId = couponId;
    }

    // Getter 메서드들
    public int getUserId() { return userId; }
    public List<OrderItemRequest> getOrderItems() { return orderItems; }
    public Integer getCouponId() { return couponId; }

    // Setter 메서드들
    public void setUserId(int userId) { this.userId = userId; }
    public void setOrderItems(List<OrderItemRequest> orderItems) { this.orderItems = orderItems; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }

    /**
     * 주문 상품 요청 DTO
     */
    public static class OrderItemRequest {
        private int productId;
        private int quantity;

        public OrderItemRequest() {}

        public OrderItemRequest(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        // Getter 메서드들
        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }

        // Setter 메서드들
        public void setProductId(int productId) { this.productId = productId; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
} 