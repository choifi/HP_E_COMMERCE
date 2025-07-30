package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class OrderItemTest {

    @Test
    void createOrderItem_성공() {
        int productId = 1;
        int quantity = 2;
        int unitPrice = 1000;

        OrderItem orderItem = new OrderItem(productId, quantity, unitPrice);

        assertThat(orderItem.getProductId()).isEqualTo(productId);
        assertThat(orderItem.getQuantity()).isEqualTo(quantity);
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderItem.getTotalPrice()).isEqualTo(2000);
    }

    @Test
    void createOrderItem_수량0_실패() {
        assertThatThrownBy(() -> new OrderItem(1, 0, 1000))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("수량은 1 이상이어야 합니다");
    }

    @Test
    void createOrderItem_음수수량_실패() {
        assertThatThrownBy(() -> new OrderItem(1, -1, 1000))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("수량은 1 이상이어야 합니다");
    }

    @Test
    void createOrderItem_단가0_실패() {
        assertThatThrownBy(() -> new OrderItem(1, 2, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("단가는 0보다 커야 합니다");
    }

    @Test
    void createOrderItem_음수단가_실패() {
        assertThatThrownBy(() -> new OrderItem(1, 2, -1000))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("단가는 0보다 커야 합니다");
    }

    @Test
    void getTotalPrice_다양한수량과단가_성공() {
        OrderItem item1 = new OrderItem(1, 3, 1000);
        OrderItem item2 = new OrderItem(2, 2, 1500);
        OrderItem item3 = new OrderItem(3, 1, 500);

        assertThat(item1.getTotalPrice()).isEqualTo(3000);
        assertThat(item2.getTotalPrice()).isEqualTo(3000);
        assertThat(item3.getTotalPrice()).isEqualTo(500);
    }

    @Test
    void getTotalPrice_큰수량_성공() {
        OrderItem item = new OrderItem(1, 1000, 1000);
        assertThat(item.getTotalPrice()).isEqualTo(1000000);
    }

    @Test
    void getTotalPrice_큰단가_성공() {
        OrderItem item = new OrderItem(1, 1, 1000000);
        assertThat(item.getTotalPrice()).isEqualTo(1000000);
    }
} 