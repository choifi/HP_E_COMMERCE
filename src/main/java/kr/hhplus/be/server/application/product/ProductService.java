package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//상품 서비스
@Service
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // 모든 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // 상품 조회
    public Product getProductById(int productId) {

        return productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));
    }

    // 상품 재고 업데이트
    @Transactional
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    // 상품 재고 차감 - 비관적 락 적용
    @Transactional
    public Product reduceStockWithLock(int productId, int quantity) {
        Product product = productRepository.findByIdWithLock(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));
        
        product.reduceStock(quantity);
        return productRepository.save(product);
    }
}