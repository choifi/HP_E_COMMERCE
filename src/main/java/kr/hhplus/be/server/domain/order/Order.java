package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private List<OrderItem> orderItems;
    private int totalAmount;
    private int discountAmount;
    private int amount;
    private OrderStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Order() {
        this.orderItems = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public Order(int userId, List<OrderItem> orderItems) {
        this();
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("주문 아이템이 없습니다.");
        }
        this.userId = userId;
        this.orderItems = new ArrayList<>(orderItems);
        calculateAmounts();
    }

    private void calculateAmounts() {
        this.totalAmount = orderItems.stream()
            .mapToInt(item -> item.getUnitPrice() * item.getQuantity())
            .sum();
        this.amount = this.totalAmount - this.discountAmount;
    }

    public void applyDiscount(int discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 0 이상이어야 합니다.");
        }
        if (discountAmount > this.totalAmount) {
            throw new IllegalArgumentException("할인 금액은 총 금액을 초과할 수 없습니다.");
        }
        this.discountAmount = discountAmount;
        this.amount = this.totalAmount - this.discountAmount;
        this.updatedTime = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("대기 중인 주문만 완료할 수 있습니다.");
        }
        this.status = OrderStatus.COMPLETED;
        this.updatedTime = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("완료된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCEL_REQUESTED;
        this.updatedTime = LocalDateTime.now();
    }

    public List<OrderItem> getOrderItems() { 
        return new ArrayList<>(orderItems); 
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getUserId() { return userId; }
    
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    
    public int getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }
    
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
} 