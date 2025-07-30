package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderFacade orderApplicationService;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(orderApplicationService);
    }

    @Test
    void createOrder_성공() {
        // Given
        int userId = 1;
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest(1, 2);
        CreateOrderRequest request = new CreateOrderRequest(userId, List.of(itemRequest), null);

        Order order = createOrder(userId, 1, 2, 1000);
        when(orderApplicationService.createOrderWithPayment(eq(userId), any(), eq(null)))
            .thenReturn(order);

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
        assertThat(response.getBody().getStatus()).isEqualTo("PENDING"); // Mock에서는 PENDING 상태
        assertThat(response.getBody().getTotalAmount()).isEqualTo(2000);
    }

    @Test
    void createOrder_쿠폰사용_성공() {
        // Given
        int userId = 1;
        int couponId = 1;
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest(1, 2);
        CreateOrderRequest request = new CreateOrderRequest(userId, List.of(itemRequest), couponId);

        Order order = createOrder(userId, 1, 2, 1000);
        order.applyDiscount(200); // 200원 할인 적용
        when(orderApplicationService.createOrderWithPayment(eq(userId), any(), eq(couponId)))
            .thenReturn(order);

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
        assertThat(response.getBody().getDiscountAmount()).isEqualTo(200);
        assertThat(response.getBody().getAmount()).isEqualTo(1800); // 2000 - 200
    }

    @Test
    void getOrderById_성공() {
        // Given
        int orderId = 1;
        int userId = 1;
        Order order = createOrder(userId, 1, 2, 1000);
        when(orderApplicationService.getOrderById(orderId)).thenReturn(order);

        // When
        ResponseEntity<OrderResponse> response = orderController.getOrderById(orderId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOrderId()).isEqualTo(orderId);
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
    }

    @Test
    void getOrdersByUserId_성공() {
        // Given
        int userId = 1;
        Order order1 = createOrder(userId, 1, 2, 1000);
        Order order2 = createOrder(userId, 2, 1, 2000);
        List<Order> orders = List.of(order1, order2);
        
        when(orderApplicationService.getOrdersByUserId(userId)).thenReturn(orders);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrdersByUserId(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getUserId()).isEqualTo(userId);
        assertThat(response.getBody().get(1).getUserId()).isEqualTo(userId);
    }

    @Test
    void cancelOrder_성공() {
        // Given
        int orderId = 1;
        Order order = createOrder(1, 1, 2, 1000);
        order.cancel(); // 취소 상태로 변경
        when(orderApplicationService.cancelOrder(orderId)).thenReturn(order);

        // When
        ResponseEntity<OrderResponse> response = orderController.cancelOrder(orderId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("CANCEL_REQUESTED"); // 문자열로 비교
    }

    @Test
    void createOrder_복잡한주문아이템_성공() {
        // Given
        int userId = 1;
        CreateOrderRequest.OrderItemRequest item1 = new CreateOrderRequest.OrderItemRequest(1, 2);
        CreateOrderRequest.OrderItemRequest item2 = new CreateOrderRequest.OrderItemRequest(2, 1);
        CreateOrderRequest.OrderItemRequest item3 = new CreateOrderRequest.OrderItemRequest(3, 3);
        List<CreateOrderRequest.OrderItemRequest> items = List.of(item1, item2, item3);
        CreateOrderRequest request = new CreateOrderRequest(userId, items, null);

        Order order = createComplexOrder(userId, items);
        when(orderApplicationService.createOrderWithPayment(eq(userId), any(), eq(null)))
            .thenReturn(order);

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOrderItems()).hasSize(3);
        assertThat(response.getBody().getTotalAmount()).isEqualTo(6000); // (2*1000) + (1*1000) + (3*1000)
    }

    // Helper methods
    private Order createOrder(int userId, int productId, int quantity, int unitPrice) {
        OrderItem orderItem = new OrderItem(productId, quantity, unitPrice);
        List<OrderItem> orderItems = List.of(orderItem);
        
        Order order = new Order(userId, orderItems);
        order.setOrderId(1);
        order.setCreatedTime(LocalDateTime.now());
        order.setUpdatedTime(LocalDateTime.now());
        // PENDING 상태로 유지 (완료는 OrderApplicationService에서 처리)
        
        return order;
    }

    private Order createComplexOrder(int userId, List<CreateOrderRequest.OrderItemRequest> items) {
        List<OrderItem> orderItems = items.stream()
            .map(item -> new OrderItem(item.getProductId(), item.getQuantity(), 1000)) // 모든 상품 1000원으로 가정
            .toList();
        
        Order order = new Order(userId, orderItems);
        order.setOrderId(1);
        order.setCreatedTime(LocalDateTime.now());
        order.setUpdatedTime(LocalDateTime.now());
        // PENDING 상태로 유지 (완료는 OrderApplicationService에서 처리)
        
        return order;
    }
} 