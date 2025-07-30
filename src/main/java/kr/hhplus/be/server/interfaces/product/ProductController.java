package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * 모든 상품 조회
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> responses = products.stream()
            .map(ProductResponse::from)
            .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 상품 ID로 상품 조회
     * GET /api/products/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable int productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(ProductResponse.from(product));
    }
} 