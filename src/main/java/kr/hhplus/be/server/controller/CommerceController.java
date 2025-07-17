package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class CommerceController implements CommerceApiSpec {
    
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 반환")
    @GetMapping("/commerce/products")
    @Override
    public ResponseEntity<List<CommerceResponse.ProductList>> getProductList(@RequestParam(required = false) Long product_id) {
        List<CommerceResponse.ProductList> products = List.of(
            new CommerceResponse.ProductList(1L, "상품1", BigDecimal.valueOf(10000)),
            new CommerceResponse.ProductList(2L, "상품2", BigDecimal.valueOf(20000)),
            new CommerceResponse.ProductList(3L, "상품3", BigDecimal.valueOf(30000))
        );
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "주문 생성", description = "주문 요청을 받아 주문을 생성")
    @PostMapping("/commerce/orders")
    @Override
    public ResponseEntity<CommerceResponse.OrderCreated> createOrder(@RequestBody CommerceRequest.CreateOrder request) {
        CommerceResponse.OrderCreated response = new CommerceResponse.OrderCreated(1L, "SUCCESS");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 요청", description = "주문에 대한 결제")
    @PostMapping("/commerce/payments")
    @Override
    public ResponseEntity<CommerceResponse.PaymentResult> paymentRequest(@RequestBody CommerceRequest.PaymentRequest request) {
        CommerceResponse.PaymentResult response = new CommerceResponse.PaymentResult(
            "PAYMENT_SUCCESS", 
            1L,
            1L
        );
        return ResponseEntity.ok(response);
    }
}