package kr.hhplus.be.server.interfaces.commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.math.BigDecimal;

/**
 * 커머스 응답 DTO
 * 외부로 노출할 데이터 형식을 정의
 * Swagger 문서화를 위한 스키마 정보 포함
 */
public class CommerceResponse {
    
    /**
     * 상품 목록 응답
     */
    public record ProductList(
            @Schema(description = "상품 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long id,
            @Schema(description = "상품 이름", requiredMode = RequiredMode.REQUIRED, example = "상품1")
            String name,
            @Schema(description = "상품 가격", requiredMode = RequiredMode.REQUIRED, example = "10000")
            BigDecimal price
    ) {
    }
    
    /**
     * 주문 생성 응답
     */
    public record OrderCreated(
            @Schema(description = "주문 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long orderId,
            @Schema(description = "주문 상태", requiredMode = RequiredMode.REQUIRED, example = "SUCCESS")
            String status
    ) {
    }
    
    /**
     * 결제 결과 응답
     */
    public record PaymentResult(
            @Schema(description = "결제 상태", requiredMode = RequiredMode.REQUIRED, example = "PAYMENT_SUCCESS")
            String status,
            @Schema(description = "주문 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long orderId,
            @Schema(description = "유저 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long userId
    ) {
    }
} 