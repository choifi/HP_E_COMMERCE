package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.product.ProductRankingService;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    private final ProductRankingService productRankingService;
    
    public ProductController(ProductService productService, ProductRankingService productRankingService) {
        this.productService = productService;
        this.productRankingService = productRankingService;
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

    // 인기 상품 TOP 10 조회
    @GetMapping("/ranking/top")
    public ResponseEntity<List<ProductResponse>> getTopRankingProducts() {
        List<Integer> topProductIds = productRankingService.getTopRankingProducts();
        
        List<ProductResponse> topProducts = topProductIds.stream()
            .map(productId -> {
                try {
                    Product product = productService.getProductById(productId);
                    return ProductResponse.from(product);
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(topProducts);
    }

    // 특정 상품 - 랭킹 정보 조회
    @GetMapping("/{productId}/ranking")
    public ResponseEntity<ProductRankingService.ProductRankingInfo> getProductRanking(@PathVariable int productId) {
        ProductRankingService.ProductRankingInfo rankingInfo = productRankingService.getProductRanking(productId);
        return ResponseEntity.ok(rankingInfo);
    }
} 