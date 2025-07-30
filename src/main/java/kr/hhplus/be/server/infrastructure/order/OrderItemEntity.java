package kr.hhplus.be.server.infrastructure.order;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// 주문 상품 JPA 엔티티
@Entity
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private int orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    public OrderItemEntity() {}

    // Getter, Setter 
    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
    
    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    // 도메인 변환 메서드
    public kr.hhplus.be.server.domain.order.OrderItem toDomain() {
        kr.hhplus.be.server.domain.order.OrderItem orderItem = 
            new kr.hhplus.be.server.domain.order.OrderItem(productId, quantity, unitPrice);
        
        orderItem.setOrderItemId(orderItemId);
        orderItem.setOrderId(order != null ? order.getOrderId() : 0);
        orderItem.setCreatedTime(createdTime);
        orderItem.setUpdatedTime(updatedTime);
        
        return orderItem;
    }

    // 도메인 객체에서 변환
    public static OrderItemEntity fromDomain(kr.hhplus.be.server.domain.order.OrderItem orderItem) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrderItemId(orderItem.getOrderItemId());
        entity.setProductId(orderItem.getProductId());
        entity.setQuantity(orderItem.getQuantity());
        entity.setUnitPrice(orderItem.getUnitPrice());
        entity.setCreatedTime(orderItem.getCreatedTime());
        entity.setUpdatedTime(orderItem.getUpdatedTime());
        
        return entity;
    }
} 