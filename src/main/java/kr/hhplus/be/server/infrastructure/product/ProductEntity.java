package kr.hhplus.be.server.infrastructure.product;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class ProductEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "price", nullable = false)
    private int price;
    
    @Column(name = "stock", nullable = false)
    private int stock;
    
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;
    
    public ProductEntity() {}
    
    // Getter, Setter
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
    
    // 도메인 객체로 변환
    public kr.hhplus.be.server.domain.product.Product toDomain() {
        kr.hhplus.be.server.domain.product.Product product = 
            new kr.hhplus.be.server.domain.product.Product(name, price, stock);
        product.setProductId(productId);
        product.setCreatedTime(createdTime);
        product.setUpdatedTime(updatedTime);
        return product;
    }
    
    // 도메인 객체에서 변환
    public static ProductEntity fromDomain(kr.hhplus.be.server.domain.product.Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setProductId(product.getProductId());
        entity.setName(product.getName());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setCreatedTime(product.getCreatedTime());
        entity.setUpdatedTime(product.getUpdatedTime());
        return entity;
    }
}