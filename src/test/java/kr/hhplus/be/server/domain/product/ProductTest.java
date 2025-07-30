package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    void createProduct_성공() {
        String name = "테스트 상품";
        int price = 1000;
        int stock = 10;

        Product product = new Product(name, price, stock);

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStock()).isEqualTo(stock);
    }

    @Test
    void createProduct_빈이름_실패() {
        assertThatThrownBy(() -> new Product("", 1000, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("상품명은 필수입니다");
    }

    @Test
    void createProduct_음수가격_실패() {
        assertThatThrownBy(() -> new Product("상품", -1000, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("가격은 0 이상이어야 합니다");
    }

    @Test
    void createProduct_음수재고_실패() {
        assertThatThrownBy(() -> new Product("상품", 1000, -10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고는 0 이상이어야 합니다");
    }

    @Test
    void hasStock_충분한재고_성공() {
        Product product = new Product("상품", 1000, 10);

        assertThat(product.hasStock(5)).isTrue();
        assertThat(product.hasStock(10)).isTrue();
    }

    @Test
    void hasStock_부족한재고_실패() {
        Product product = new Product("상품", 1000, 10);

        assertThat(product.hasStock(15)).isFalse();
    }

    @Test
    void reduceStock_성공() {
        Product product = new Product("상품", 1000, 10);

        product.reduceStock(5);

        assertThat(product.getStock()).isEqualTo(5);
    }

    @Test
    void reduceStock_부족한재고_실패() {
        Product product = new Product("상품", 1000, 10);

        assertThatThrownBy(() -> product.reduceStock(15))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    void reduceStock_음수수량_실패() {
        Product product = new Product("상품", 1000, 10);

        assertThatThrownBy(() -> product.reduceStock(-5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("차감할 수량은 0보다 커야 합니다");
    }

    @Test
    void reduceStock_0수량_실패() {
        Product product = new Product("상품", 1000, 10);

        assertThatThrownBy(() -> product.reduceStock(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("차감할 수량은 0보다 커야 합니다");
    }
} 