package kr.hhplus.be.server.integration.product;

import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class ProductIntegrationTest {

    @Autowired
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    @Transactional
    void setUp() {
        // 테스트용 상품 생성
        testProduct = new Product("테스트상품", 10000, 10);
        testProduct = productService.updateProduct(testProduct);
    }

    @Test
    @Transactional
    void 상품_생성_성공() {
        // given
        Product product = new Product("새상품", 15000, 20);

        // when
        Product savedProduct = productService.updateProduct(product);

        // then
        assertThat(savedProduct.getProductId()).isGreaterThan(0);
        assertThat(savedProduct.getName()).isEqualTo("새상품");
        assertThat(savedProduct.getPrice()).isEqualTo(15000);
        assertThat(savedProduct.getStock()).isEqualTo(20);
    }

    @Test
    @Transactional
    void 상품_조회_성공() {
        // when
        Product foundProduct = productService.getProductById(testProduct.getProductId());

        // then
        assertThat(foundProduct.getProductId()).isEqualTo(testProduct.getProductId());
        assertThat(foundProduct.getName()).isEqualTo("테스트상품");
        assertThat(foundProduct.getPrice()).isEqualTo(10000);
        assertThat(foundProduct.getStock()).isEqualTo(10);
    }

    @Test
    @Transactional
    void 상품_조회_실패_존재하지_않는_상품() {
        // when & then
        assertThatThrownBy(() -> productService.getProductById(999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품을 찾을 수 없습니다. ID: 999");
    }

    @Test
    @Transactional
    void 모든_상품_조회_성공() {
        // given
        Product product1 = new Product("상품1", 5000, 5);
        Product product2 = new Product("상품2", 8000, 8);
        productService.updateProduct(product1);
        productService.updateProduct(product2);

        // when
        List<Product> products = productService.getAllProducts();

        // then
        assertThat(products).hasSizeGreaterThanOrEqualTo(3); // setUp의 testProduct + 2개
        assertThat(products).anyMatch(p -> p.getName().equals("테스트상품"));
        assertThat(products).anyMatch(p -> p.getName().equals("상품1"));
        assertThat(products).anyMatch(p -> p.getName().equals("상품2"));
    }

    @Test
    @Transactional
    void 상품_재고_관리_성공() {
        // when - 재고 차감
        testProduct.reduceStock(3);
        Product updatedProduct = productService.updateProduct(testProduct);

        // then
        assertThat(updatedProduct.getStock()).isEqualTo(7);
    }

    @Test
    @Transactional
    void 상품_재고_부족_예외() {
        // when & then
        assertThatThrownBy(() -> testProduct.reduceStock(15))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @Transactional
    void 상품_재고_충분_확인() {
        // when & then
        assertThat(testProduct.hasStock(5)).isTrue();
        assertThat(testProduct.hasStock(10)).isTrue();
        assertThat(testProduct.hasStock(15)).isFalse();
    }

    @Test
    @Transactional
    void 상품_재고_음수_차감_예외() {
        // when & then
        assertThatThrownBy(() -> testProduct.reduceStock(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("차감할 수량은 0보다 커야 합니다");
    }

    @Test
    @Transactional
    void 상품_재고_0개_차감_예외() {
        // when & then
        assertThatThrownBy(() -> testProduct.reduceStock(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("차감할 수량은 0보다 커야 합니다");
    }

    @Test
    @Transactional
    void 상품_가격_업데이트_성공() {
        // given
        testProduct.setPrice(12000);

        // when
        Product updatedProduct = productService.updateProduct(testProduct);

        // then
        assertThat(updatedProduct.getPrice()).isEqualTo(12000);
        assertThat(updatedProduct.getName()).isEqualTo("테스트상품");
        assertThat(updatedProduct.getStock()).isEqualTo(10);
    }

    @Test
    @Transactional
    void 상품_재고_업데이트_성공() {
        // given
        testProduct.setStock(25);

        // when
        Product updatedProduct = productService.updateProduct(testProduct);

        // then
        assertThat(updatedProduct.getStock()).isEqualTo(25);
        assertThat(updatedProduct.getName()).isEqualTo("테스트상품");
        assertThat(updatedProduct.getPrice()).isEqualTo(10000);
    }

    @Test
    void 재고_정확성_동시성_테스트() throws InterruptedException {
        // given - 재고 10개
        final Product product = new Product("재고테스트", 1000, 10);
        final Product savedProduct = productService.updateProduct(product);

        // when - 10명이 동시에 1개씩 구매
        int count = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(count);

        ExecutorService executor = Executors.newFixedThreadPool(count);

        for (int i = 0; i < count; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    // 재고 차감 시도
                    Product currentProduct = productService.getProductById(savedProduct.getProductId());
                    currentProduct.reduceStock(1);
                    productService.updateProduct(currentProduct);
                } catch (Exception e) {
                    System.out.println("사용자 " + userId + " 구매 실패: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        // then - 정확히 0개가 남아야 함
        Product finalProduct = productService.getProductById(savedProduct.getProductId());
        
        System.out.println("초기 재고: " + savedProduct.getStock());
        System.out.println("최종 재고: " + finalProduct.getStock());
        
        assertThat(finalProduct.getStock()).isEqualTo(0);
    }
} 