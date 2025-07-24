package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;

public class Product {
    private int productId;
    private String name;
    private int price;
    private int stock;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Product() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public Product(String name, int price, int stock) {
        this();
        validate(name, price, stock);
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    private void validate(String name, int price, int stock) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }

    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감할 수량은 0보다 커야 합니다.");
        }
        if (!hasStock(quantity)) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock -= quantity;
        this.updatedTime = LocalDateTime.now();
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}