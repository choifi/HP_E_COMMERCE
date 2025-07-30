package kr.hhplus.be.server.interfaces.product;

// 상품 수정 요청 DTO
public class UpdateProductRequest {
    
    private String name;
    private int price;
    private int stock;
    
    public UpdateProductRequest() {}
    
    // 생성자
    public UpdateProductRequest(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // Getter 
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }
    
    // Setter 
    public void setName(String name) { this.name = name; }
    public void setPrice(int price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
} 