package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "커머스", description = "커머스 관련 API")
public interface CommerceApiSpec {

    @Operation(summary = "상품 목록 조회")
    ResponseEntity<List<CommerceResponse.ProductList>> getProductList(@RequestParam(required = false) Long product_id);

    @Operation(summary = "주문 생성")
    ResponseEntity<CommerceResponse.OrderCreated> createOrder(CommerceRequest.CreateOrder request);

    @Operation(summary = "결제 요청")
    ResponseEntity<CommerceResponse.PaymentResult> paymentRequest(CommerceRequest.PaymentRequest request);
}