package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    
    // 모든 상품 조회
    List<Product> findAll();
    
    // 상품 ID로 상품 조회
    Optional<Product> findById(int productId);
    Optional<Product> findByIdWithLock(int productId);
    
    // 상품 업데이트
    Product save(Product product);
}