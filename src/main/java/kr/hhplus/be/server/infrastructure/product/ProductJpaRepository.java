package kr.hhplus.be.server.infrastructure.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {
    
    // 모든 상품 조회
    @Override
    @Query("SELECT p FROM ProductEntity p ORDER BY p.productId")
    List<ProductEntity> findAll();
    
    // 상품 ID로 조회
    @Override
    @Query("SELECT p FROM ProductEntity p WHERE p.productId = :productId")
    Optional<ProductEntity> findById(Integer productId);
}