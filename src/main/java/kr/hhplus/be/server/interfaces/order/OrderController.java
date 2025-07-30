package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.order.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderFacade orderApplicationService;

    public OrderController(OrderFacade orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    // 주문 생성 및 결제
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        List<OrderFacade.CreateOrderItemRequest> orderItems = request.getOrderItems().stream()
            .map(item -> new OrderFacade.CreateOrderItemRequest(item.getProductId(), item.getQuantity()))
            .toList();

        Order order = orderApplicationService.createOrderWithPayment(
            request.getUserId(), 
            orderItems, 
            request.getCouponId()
        );

        return ResponseEntity.ok(OrderResponse.from(order));
    }

    // 주문 ID로 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable int orderId) {
        Order order = orderApplicationService.getOrderById(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    // 사용자 ID로 주문 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable int userId) {
        List<Order> orders = orderApplicationService.getOrdersByUserId(userId);
        List<OrderResponse> responses = orders.stream()
            .map(OrderResponse::from)
            .toList();

        return ResponseEntity.ok(responses);
    }

    // 주문 취소
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable int orderId) {
        Order order = orderApplicationService.cancelOrder(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
} 