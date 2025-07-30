package kr.hhplus.be.server.interfaces.commerce;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.commerce.dto.CommerceRequest;
import kr.hhplus.be.server.interfaces.commerce.dto.CommerceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 커머스 API 스펙 인터페이스
 * Swagger 문서화를 위한 API 계약 정의
 * Interfaces Layer의 일부
 */
@Tag(name = "커머스", description = "커머스 관련 API")
public interface CommerceApiSpec {

    @Operation(summary = "상품 목록 조회")
    ResponseEntity<List<CommerceResponse.ProductList>> getProductList(@RequestParam(required = false) Long product_id);

    @Operation(summary = "주문 생성")
    ResponseEntity<CommerceResponse.OrderCreated> createOrder(CommerceRequest.CreateOrder request);

    @Operation(summary = "결제 요청")
    ResponseEntity<CommerceResponse.PaymentResult> paymentRequest(CommerceRequest.PaymentRequest request);
} 