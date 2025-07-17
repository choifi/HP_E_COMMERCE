package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.math.BigDecimal;

public class CommerceResponse {
    
    public record ProductList(
            @Schema(description = "상품 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long id,
            @Schema(description = "상품 이름", requiredMode = RequiredMode.REQUIRED, example = "상품1")
            String name,
            @Schema(description = "상품 가격", requiredMode = RequiredMode.REQUIRED, example = "10000")
            BigDecimal price
    ) {
    }
    
    public record OrderCreated(
            @Schema(description = "주문 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long orderId,
            @Schema(description = "주문 상태", requiredMode = RequiredMode.REQUIRED, example = "SUCCESS")
            String status
    ) {
    }
    
    public record PaymentResult(
            @Schema(description = "결제 상태", requiredMode = RequiredMode.REQUIRED, example = "PAYMENT_SUCCESS")
            String status,
            @Schema(description = "주문 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long orderId,
            @Schema(description = "유저 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long userId
    )
    {
    }
}
