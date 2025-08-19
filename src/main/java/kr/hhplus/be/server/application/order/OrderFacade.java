package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.common.lock.DistributedLock;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.payment.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentService paymentService;

    public OrderFacade(OrderService orderService,
                       ProductService productService,
                       PaymentService paymentService) {
        this.orderService = orderService;
        this.productService = productService;
        this.paymentService = paymentService;
    }

 
    // 재고 차감에 분산락 적용
    @DistributedLock(
        key = "'product:' + #sortedProductIds",
        waitTime = 5,
        leaseTime = 20,
        timeUnit = TimeUnit.SECONDS
    )
    @Transactional
    public Order createOrderWithPayment(int userId, 
                                      List<CreateOrderItemRequest> orderItems, 
                                      Integer couponId) {
        
        String sortedProductIds = orderItems.stream()
            .map(CreateOrderItemRequest::getProductId)
            .sorted()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
        
        validateOrderItems(orderItems);
        List<OrderItem> items = createOrderItems(orderItems);
        
        Order order = new Order(userId, items);
        
        if (couponId != null) {
            int discountAmount = paymentService.calculateDiscount(order.getTotalAmount(), couponId);
            order.applyDiscount(discountAmount);
        }
        
        decreaseProductStock(orderItems);
        
        paymentService.deductUserPoints(userId, order.getAmount());
        
        Order savedOrder = orderService.save(order);
        
        paymentService.processPayment(savedOrder, couponId);
        
        savedOrder.complete();
        return orderService.save(savedOrder);
    }

    public Order getOrderById(int orderId) {
        return orderService.findById(orderId);
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orderService.findByUserId(userId);
    }

    @Transactional
    public Order cancelOrder(int orderId) {
        return orderService.cancelOrder(orderId);
    }

    private void validateOrderItems(List<CreateOrderItemRequest> orderItemRequests) {
        orderItemRequests.forEach(this::validateOrderItem);
    }

    private void validateOrderItem(CreateOrderItemRequest request) {
        Product product = productService.getProductById(request.getProductId());
        
        if (!product.hasStock(request.getQuantity())) {
            throw new IllegalArgumentException(
                "재고가 부족합니다. 상품: " + product.getName() + 
                ", 요청 수량: " + request.getQuantity() + 
                ", 현재 재고: " + product.getStock()
            );
        }
    }

    private List<OrderItem> createOrderItems(List<CreateOrderItemRequest> orderItemRequests) {
        return orderItemRequests.stream()
            .map(this::createOrderItem)
            .collect(Collectors.toList());
    }

    private OrderItem createOrderItem(CreateOrderItemRequest request) {
        Product product = productService.getProductById(request.getProductId());
        return new OrderItem(request.getProductId(), request.getQuantity(), product.getPrice());
    }

    private void decreaseProductStock(List<CreateOrderItemRequest> orderItems) {
        for (CreateOrderItemRequest item : orderItems) {
            Product product = productService.getProductById(item.getProductId());
            product.reduceStock(item.getQuantity());
            productService.updateProduct(product);
        }
    }

    public static class CreateOrderItemRequest {
        private int productId;
        private int quantity;

        public CreateOrderItemRequest(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
    }
} 