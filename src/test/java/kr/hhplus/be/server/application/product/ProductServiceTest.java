package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * ProductService 단위테스트
 * 조회 기능만 테스트
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    @DisplayName("모든 상품 조회 성공")
    void getAllProducts_Success() {
        // given
        List<Product> expectedProducts = Arrays.asList(
            new Product("상품1", 10000, 10),
            new Product("상품2", 20000, 5)
        );
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // when
        List<Product> actualProducts = productService.getAllProducts();

        // then
        assertThat(actualProducts).hasSize(2);
        assertThat(actualProducts.get(0).getName()).isEqualTo("상품1");
        assertThat(actualProducts.get(1).getName()).isEqualTo("상품2");
    }

    @Test
    @DisplayName("상품 ID로 조회 성공")
    void getProductById_Success() {
        // given
        Product expectedProduct = new Product("상품1", 10000, 10);
        expectedProduct.setProductId(1);
        when(productRepository.findById(1)).thenReturn(Optional.of(expectedProduct));

        // when
        Product actualProduct = productService.getProductById(1);

        // then
        assertThat(actualProduct.getName()).isEqualTo("상품1");
        assertThat(actualProduct.getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("상품 ID로 조회 실패 - 상품이 존재하지 않음")
    void getProductById_NotFound() {
        // given
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProductById(999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품을 찾을 수 없습니다. ID: 999");
    }

} 