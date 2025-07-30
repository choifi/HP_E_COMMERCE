package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.Product;

import java.time.LocalDateTime;

/**
 * 상품 응답 DTO
 * 외부로 노출할 데이터 형식을 정의
 * Domain 객체와 분리하여 API 스펙 변경에 유연하게 대응
 */
public class ProductResponse {
    
    private final int productId;
    private final String name;
    private final int price;
    private final int stock;
    private final LocalDateTime createdTime;
    private final LocalDateTime updatedTime;
    
    public ProductResponse(int productId, String name, int price, int stock, 
                          LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
    
    /**
     * Domain 객체에서 Response DTO로 변환
     * @param product 도메인 객체
     * @return 응답 DTO
     */
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getProductId(),
            product.getName(),
            product.getPrice(),
            product.getStock(),
            product.getCreatedTime(),
            product.getUpdatedTime()
        );
    }
    
    // Getter 메서드들
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
}
 