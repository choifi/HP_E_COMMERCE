package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;

    private OrderApplicationService orderApplicationService;

    @BeforeEach
    void setUp() {
        orderApplicationService = new OrderApplicationService(orderRepository, productService, paymentService);
    }

    @Test
    void createOrderWithPayment_성공() {
        int userId = 1;
        int productId = 1;
        int quantity = 2;
        int productPrice = 1000;
        int productStock = 10;
        
        OrderApplicationService.OrderItemRequest orderItemRequest = 
            new OrderApplicationService.OrderItemRequest(productId, quantity);
        List<OrderApplicationService.OrderItemRequest> orderItems = List.of(orderItemRequest);
        
        Product product = createProduct(productId, "테스트 상품", productPrice, productStock);
        Order pendingOrder = createOrder(userId, productId, quantity, productPrice);
        Order completedOrder = createOrder(userId, productId, quantity, productPrice);
        completedOrder.complete();
        Payment payment = createPayment(completedOrder.getOrderId(), productPrice * quantity, 0, null);
        
        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.updateProduct(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder, completedOrder);
        when(paymentService.processPayment(any(Order.class), any())).thenReturn(payment);
        
        Order result = orderApplicationService.createOrderWithPayment(userId, orderItems, null);
        
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getTotalAmount()).isEqualTo(productPrice * quantity);
        assertThat(result.getAmount()).isEqualTo(productPrice * quantity);
        
        verify(productService, times(2)).getProductById(productId);
        verify(productService).updateProduct(any(Product.class));
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(paymentService).processPayment(any(Order.class), eq(null));
    }

    @Test
    void createOrderWithPayment_재고부족_실패() {
        int userId = 1;
        int productId = 1;
        int quantity = 15;
        int productPrice = 1000;
        int productStock = 10;
        
        OrderApplicationService.OrderItemRequest orderItemRequest = 
            new OrderApplicationService.OrderItemRequest(productId, quantity);
        List<OrderApplicationService.OrderItemRequest> orderItems = List.of(orderItemRequest);
        
        Product product = createProduct(productId, "테스트 상품", productPrice, productStock);
        
        when(productService.getProductById(productId)).thenReturn(product);
        
        assertThatThrownBy(() -> orderApplicationService.createOrderWithPayment(userId, orderItems, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    void createOrderWithPayment_쿠폰할인_성공() {
        int userId = 1;
        int productId = 1;
        int quantity = 2;
        int productPrice = 1000;
        int productStock = 10;
        int couponId = 1;
        int discountAmount = 200;
        
        OrderApplicationService.OrderItemRequest orderItemRequest = 
            new OrderApplicationService.OrderItemRequest(productId, quantity);
        List<OrderApplicationService.OrderItemRequest> orderItems = List.of(orderItemRequest);
        
        Product product = createProduct(productId, "테스트 상품", productPrice, productStock);
        Order pendingOrder = createOrder(userId, productId, quantity, productPrice);
        Order completedOrder = createOrder(userId, productId, quantity, productPrice);
        completedOrder.applyDiscount(discountAmount);
        completedOrder.complete();
        Payment payment = createPayment(completedOrder.getOrderId(), productPrice * quantity, discountAmount, couponId);
        
        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.updateProduct(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder, completedOrder);
        when(paymentService.calculateDiscount(anyInt(), eq(couponId))).thenReturn(discountAmount);
        when(paymentService.processPayment(any(Order.class), eq(couponId))).thenReturn(payment);
        
        Order result = orderApplicationService.createOrderWithPayment(userId, orderItems, couponId);
        
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getTotalAmount()).isEqualTo(productPrice * quantity);
        assertThat(result.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(result.getAmount()).isEqualTo(productPrice * quantity - discountAmount);
        
        verify(productService, times(2)).getProductById(productId);
        verify(productService).updateProduct(any(Product.class));
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(paymentService).calculateDiscount(productPrice * quantity, couponId);
        verify(paymentService).processPayment(any(Order.class), eq(couponId));
    }

    @Test
    void getOrderById_성공() {
        int orderId = 1;
        int userId = 1;
        Order order = createOrder(userId, 1, 2, 1000);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        Order result = orderApplicationService.getOrderById(orderId);
        
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getUserId()).isEqualTo(userId);
        
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_존재하지않음_실패() {
        int orderId = 999;
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> orderApplicationService.getOrderById(orderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("주문을 찾을 수 없습니다");
    }

    @Test
    void getOrdersByUserId_성공() {
        int userId = 1;
        Order order1 = createOrder(userId, 1, 2, 1000);
        Order order2 = createOrder(userId, 2, 1, 2000);
        List<Order> orders = List.of(order1, order2);
        
        when(orderRepository.findByUserId(userId)).thenReturn(orders);
        
        List<Order> result = orderApplicationService.getOrdersByUserId(userId);
        
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(1).getUserId()).isEqualTo(userId);
        
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    void cancelOrder_성공() {
        int orderId = 1;
        int userId = 1;
        Order order = createOrder(userId, 1, 2, 1000);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        Order result = orderApplicationService.cancelOrder(orderId);
        
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUESTED);
        
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    private Product createProduct(int productId, String name, int price, int stock) {
        Product product = new Product(name, price, stock);
        product.setProductId(productId);
        product.setCreatedTime(LocalDateTime.now());
        product.setUpdatedTime(LocalDateTime.now());
        return product;
    }
    
    private Order createOrder(int userId, int productId, int quantity, int unitPrice) {
        OrderItem orderItem = new OrderItem(productId, quantity, unitPrice);
        List<OrderItem> orderItems = List.of(orderItem);
        
        Order order = new Order(userId, orderItems);
        order.setOrderId(1);
        order.setCreatedTime(LocalDateTime.now());
        order.setUpdatedTime(LocalDateTime.now());
        
        return order;
    }
    
    private Payment createPayment(int orderId, int totalAmount, int discountAmount, Integer couponId) {
        Payment payment = new Payment(orderId, totalAmount, discountAmount, couponId);
        payment.setPaymentId(1);
        payment.setCreatedTime(LocalDateTime.now());
        payment.setUpdatedTime(LocalDateTime.now());
        return payment;
    }
} 