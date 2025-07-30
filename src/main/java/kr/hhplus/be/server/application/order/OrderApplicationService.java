package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.payment.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;

    public OrderApplicationService(OrderRepository orderRepository,
                                 ProductService productService,
                                 PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @Transactional
    public Order createOrderWithPayment(int userId, 
                                      List<OrderItemRequest> orderItems, 
                                      Integer couponId) {
        List<OrderItem> items = validateAndCreateOrderItems(orderItems);
        
        Order order = new Order(userId, items);
        
        if (couponId != null) {
            int discountAmount = paymentService.calculateDiscount(order.getTotalAmount(), couponId);
            order.applyDiscount(discountAmount);
        }
        
        decreaseProductStock(orderItems);
        
        paymentService.deductUserPoints(userId, order.getAmount());
        
        Order savedOrder = orderRepository.save(order);
        
        paymentService.processPayment(savedOrder, couponId);
        
        savedOrder.complete();
        return orderRepository.save(savedOrder);
    }

    public Order getOrderById(int orderId) {
    
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + orderId));
    }

    public List<Order> getOrdersByUserId(int userId) {
    
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order cancelOrder(int orderId) {
        Order order = getOrderById(orderId);
        order.cancel();
        return orderRepository.save(order);
    }

    private List<OrderItem> validateAndCreateOrderItems(List<OrderItemRequest> orderItemRequests) {
        return orderItemRequests.stream()
            .map(this::validateAndCreateOrderItem)
            .collect(Collectors.toList());
    }

    private OrderItem validateAndCreateOrderItem(OrderItemRequest request) {
        Product product = productService.getProductById(request.getProductId());
        
        if (!product.hasStock(request.getQuantity())) {
            throw new IllegalArgumentException(
                "재고가 부족합니다. 상품: " + product.getName() + 
                ", 요청 수량: " + request.getQuantity() + 
                ", 현재 재고: " + product.getStock()
            );
        }
        
        return new OrderItem(product.getProductId(), request.getQuantity(), product.getPrice());
    }

    private void decreaseProductStock(List<OrderItemRequest> orderItems) {
        for (OrderItemRequest item : orderItems) {
            Product product = productService.getProductById(item.getProductId());
            product.reduceStock(item.getQuantity());
            productService.updateProduct(product);
        }
    }

    public static class OrderItemRequest {
        private int productId;
        private int quantity;

        public OrderItemRequest(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
    }
} 