package kr.hhplus.be.server.interfaces.commerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 커머스 요청 DTO
 * 외부에서 받는 데이터 형식을 정의
 * Swagger 문서화를 위한 스키마 정보 포함
 */
public class CommerceRequest {
    
    /**
     * 주문 생성 요청
     */
    public record CreateOrder(
            @Schema(description = "주문할 상품 ID", requiredMode = RequiredMode.REQUIRED, example = "[1, 2]")
            List<Long> productIds,

            @Schema(description = "유저 ID", requiredMode = RequiredMode.REQUIRED, example = "user123")
            String userId
    ) {
    }
    
    /**
     * 결제 요청
     */
    public record PaymentRequest(
            @Schema(description = "주문 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long orderId,

            @Schema(description = "결제 금액", requiredMode = RequiredMode.REQUIRED, example = "30000")
            BigDecimal amount
    ) {
    }
} 