package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    
    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll()
            .stream()
            .map(ProductEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(int productId) {
        return productJpaRepository.findById(productId)
            .map(ProductEntity::toDomain);
    }

    @Override
    public Optional<Product> findByIdWithLock(int productId) {
        return productJpaRepository.findByIdWithLock(productId)
            .map(ProductEntity::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductEntity.fromDomain(product);
        ProductEntity savedEntity = productJpaRepository.save(entity);
        return savedEntity.toDomain();
    }
}