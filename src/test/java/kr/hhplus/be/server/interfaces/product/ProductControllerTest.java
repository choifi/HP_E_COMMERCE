package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * ProductController 단위테스트
 * 표현 로직만 테스트
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
    }

    @Test
    @DisplayName("모든 상품 조회 성공")
    void getAllProducts_Success() {
        // given
        List<Product> products = Arrays.asList(
            new Product("상품1", 10000, 10),
            new Product("상품2", 20000, 5)
        );
        when(productService.getAllProducts()).thenReturn(products);

        // when
        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getName()).isEqualTo("상품1");
        assertThat(response.getBody().get(1).getName()).isEqualTo("상품2");
    }

    @Test
    @DisplayName("상품 ID로 조회 성공")
    void getProductById_Success() {
        // given
        Product product = new Product("상품1", 10000, 10);
        product.setProductId(1);
        when(productService.getProductById(1)).thenReturn(product);

        // when
        ResponseEntity<ProductResponse> response = productController.getProductById(1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getProductId()).isEqualTo(1);
        assertThat(response.getBody().getName()).isEqualTo("상품1");
        assertThat(response.getBody().getPrice()).isEqualTo(10000);
    }
} 