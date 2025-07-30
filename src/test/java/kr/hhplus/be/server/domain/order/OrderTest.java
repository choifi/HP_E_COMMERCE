package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void createOrder_성공() {
        int userId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 1000);
        Order order = new Order(userId, List.of(orderItem));

        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getTotalAmount()).isEqualTo(2000);
        assertThat(order.getDiscountAmount()).isEqualTo(0);
        assertThat(order.getAmount()).isEqualTo(2000);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void createOrder_빈주문아이템_실패() {
        int userId = 1;
        
        assertThatThrownBy(() -> new Order(userId, List.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("주문 아이템이 없습니다");
    }

    @Test
    void applyDiscount_성공() {
        int userId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 1000);
        Order order = new Order(userId, List.of(orderItem));
        int discountAmount = 500;

        order.applyDiscount(discountAmount);

        assertThat(order.getTotalAmount()).isEqualTo(2000);
        assertThat(order.getDiscountAmount()).isEqualTo(500);
        assertThat(order.getAmount()).isEqualTo(1500);
    }

    @Test
    void applyDiscount_할인금액이총금액보다큰경우_실패() {
        int userId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 1000);
        Order order = new Order(userId, List.of(orderItem));
        int discountAmount = 3000;

        assertThatThrownBy(() -> order.applyDiscount(discountAmount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("할인 금액은 총 금액을 초과할 수 없습니다");
    }

    @Test
    void complete_성공() {
        int userId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 1000);
        Order order = new Order(userId, List.of(orderItem));

        order.complete();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void cancel_성공() {
        int userId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 1000);
        Order order = new Order(userId, List.of(orderItem));

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUESTED);
    }

    @Test
    void getOrderItems_방어적복사_확인() {
        int userId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 1000);
        Order order = new Order(userId, List.of(orderItem));

        List<OrderItem> returnedItems = order.getOrderItems();
        returnedItems.add(new OrderItem(2, 1, 2000));

        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(returnedItems).hasSize(2);
    }

    @Test
    void calculateAmounts_복잡한주문_성공() {
        int userId = 1;
        OrderItem item1 = new OrderItem(1, 3, 1000);
        OrderItem item2 = new OrderItem(2, 2, 1500);
        OrderItem item3 = new OrderItem(3, 1, 500);
        List<OrderItem> orderItems = List.of(item1, item2, item3);

        Order order = new Order(userId, orderItems);

        assertThat(order.getTotalAmount()).isEqualTo(6500);
        assertThat(order.getDiscountAmount()).isEqualTo(0);
        assertThat(order.getAmount()).isEqualTo(6500);
    }

    @Test
    void applyDiscount_음수할인_실패() {
        int userId = 1;
        OrderItem orderItem = new OrderItem(1, 2, 1000);
        Order order = new Order(userId, List.of(orderItem));
        int negativeDiscount = -100;

        assertThatThrownBy(() -> order.applyDiscount(negativeDiscount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("할인 금액은 0 이상이어야 합니다");
    }
} 