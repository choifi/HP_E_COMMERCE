package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderService;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderPaymentIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PointService pointService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    private Product testProduct;
    private int couponId;

    @BeforeEach
    void setUp() {
        // 상품 생성
        testProduct = new Product("상품", 10000, 10);
        testProduct = productService.updateProduct(testProduct);

        // 포인트 충전
        pointService.chargePoint(1, 100000);

        // 쿠폰 정책 생성 (테스트용)
        CouponPolicy policy = new CouponPolicy(
            "20% 할인", 
            20, 
            30, 
            1000, 
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(30)
        );
        policy = couponPolicyRepository.save(policy);
        
        // 쿠폰 발급
        Coupon coupon = couponService.issueCoupon(1, policy.getPolicyId());
        couponId = coupon.getCouponId();
    }

    @Test
    void 주문_결제_전체_성공() {
        // given
        List<OrderFacade.CreateOrderItemRequest> orderItems = Arrays.asList(
            new OrderFacade.CreateOrderItemRequest(testProduct.getProductId(), 2)
        );
        
        // when
        Order order = orderFacade.createOrderWithPayment(1, orderItems, null);

        // then
        assertThat(order.getTotalAmount()).isEqualTo(20000);
        assertThat(order.getAmount()).isEqualTo(20000);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        // 재고 확인
        Product product = productService.getProductById(testProduct.getProductId());
        assertThat(product.getStock()).isEqualTo(8);

        // 포인트 확인
        Point point = pointService.getPointByUserId(1);
        assertThat(point.getCurrentPoint()).isEqualTo(80000);
    }

    @Test
    void 주문_실패_재고부족() {
        // given
        List<OrderFacade.CreateOrderItemRequest> orderItems = Arrays.asList(
            new OrderFacade.CreateOrderItemRequest(testProduct.getProductId(), 15)
        );

        // when & then
        assertThatThrownBy(() -> orderFacade.createOrderWithPayment(1, orderItems, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    void 주문_실패_포인트부족() {
        // given
        pointService.usePoint(1, 100000);
        List<OrderFacade.CreateOrderItemRequest> orderItems = Arrays.asList(
            new OrderFacade.CreateOrderItemRequest(testProduct.getProductId(), 1)
        );

        // when & then
        assertThatThrownBy(() -> orderFacade.createOrderWithPayment(1, orderItems, null))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("포인트가 부족합니다");
    }

    @Test
    void 쿠폰_주문_성공() {
        // given
        List<OrderFacade.CreateOrderItemRequest> orderItems = Arrays.asList(
            new OrderFacade.CreateOrderItemRequest(testProduct.getProductId(), 2)
        );

        // when
        Order order = orderFacade.createOrderWithPayment(1, orderItems, couponId);

        // then
        assertThat(order.getTotalAmount()).isEqualTo(20000);
        assertThat(order.getDiscountAmount()).isEqualTo(4000); // 20% 할인
        assertThat(order.getAmount()).isEqualTo(16000);

        // 포인트 확인
        Point point = pointService.getPointByUserId(1);
        assertThat(point.getCurrentPoint()).isEqualTo(84000);

        assertThat(order.getDiscountAmount()).isGreaterThan(0);
    }

    @Test
    void 주문_조회_성공() {
        // given - 주문생성
        Order order = orderFacade.createOrderWithPayment(1, 
            List.of(new OrderFacade.CreateOrderItemRequest(testProduct.getProductId(), 1)), 
            null);

        // when
        List<Order> orderList = orderFacade.getOrdersByUserId(1);

        // then
        assertThat(orderList).hasSize(1);
        assertThat(orderList.get(0).getOrderId()).isEqualTo(order.getOrderId());
    }

    @Test
    void 주문_취소_성공() {
        // given
        List<OrderItem> orderItems = Arrays.asList(
            new OrderItem(testProduct.getProductId(), 1, 10000)
        );
        
        // 주문만 생성하고 결제는 하지 않는다.
        Order order = new Order(1, orderItems);
        order = orderService.save(order);

        // when
        Order cancelledOrder = orderFacade.cancelOrder(order.getOrderId());

        // then
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUESTED);
    }


    @Test
    void 주문_상태_변경_성공() {
        // given
        List<OrderFacade.CreateOrderItemRequest> orderItems = Arrays.asList(
            new OrderFacade.CreateOrderItemRequest(testProduct.getProductId(), 1)
        );
        
        // when
        Order order = orderFacade.createOrderWithPayment(1, orderItems, null);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(order.getTotalAmount()).isEqualTo(10000);
    }
}