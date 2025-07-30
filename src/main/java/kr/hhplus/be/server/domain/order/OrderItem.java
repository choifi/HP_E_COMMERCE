package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private int quantity;
    private int unitPrice;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public OrderItem() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public OrderItem(int productId, int quantity, int unitPrice) {
        this();
        validate(productId, quantity, unitPrice);
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    private void validate(int productId, int quantity, int unitPrice) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("단가는 0보다 커야 합니다.");
        }
    }

    public int getTotalPrice() {
        return quantity * unitPrice;
    }

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
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
} 