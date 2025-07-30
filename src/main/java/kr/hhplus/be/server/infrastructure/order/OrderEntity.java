package kr.hhplus.be.server.infrastructure.order;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private kr.hhplus.be.server.domain.order.OrderStatus status;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public OrderEntity() {}

    // Getter, Setter
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    
    public int getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }
    
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    public kr.hhplus.be.server.domain.order.OrderStatus getStatus() { return status; }
    public void setStatus(kr.hhplus.be.server.domain.order.OrderStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
    
    public List<OrderItemEntity> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemEntity> orderItems) { this.orderItems = orderItems; }

    // 도메인 변환 메서드
    public kr.hhplus.be.server.domain.order.Order toDomain() {
        kr.hhplus.be.server.domain.order.Order order = 
            new kr.hhplus.be.server.domain.order.Order(userId, 
                orderItems.stream()
                    .map(OrderItemEntity::toDomain)
                    .collect(Collectors.toList()));
        
        order.setOrderId(orderId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setAmount(amount);
        order.setStatus(status);
        order.setCreatedTime(createdTime);
        order.setUpdatedTime(updatedTime);
        
        return order;
    }

    // 도메인 객체에서 변환
    public static OrderEntity fromDomain(kr.hhplus.be.server.domain.order.Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(order.getOrderId());
        entity.setUserId(order.getUserId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setDiscountAmount(order.getDiscountAmount());
        entity.setAmount(order.getAmount());
        entity.setStatus(order.getStatus());
        entity.setCreatedTime(order.getCreatedTime());
        entity.setUpdatedTime(order.getUpdatedTime());
        
        // OrderItem 변환
        List<OrderItemEntity> orderItemEntities = order.getOrderItems().stream()
            .map(OrderItemEntity::fromDomain)
            .collect(Collectors.toList());
        
        for (OrderItemEntity itemEntity : orderItemEntities) {
            itemEntity.setOrder(entity);
        }
        entity.setOrderItems(orderItemEntities);
        
        return entity;
    }
} 