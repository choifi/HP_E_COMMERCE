package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.math.BigDecimal;
import java.util.List;

public class CommerceRequest {
    
    public record CreateOrder(
            @Schema(description = "주문할 상품 ID", requiredMode = RequiredMode.REQUIRED, example = "[1, 2]")
            List<Long> productIds,

            @Schema(description = "유저 ID", requiredMode = RequiredMode.REQUIRED, example = "user123")
            String userId
    ) {
    }
    
    public record PaymentRequest(
            @Schema(description = "주문 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
            Long orderId,

            @Schema(description = "결제 금액", requiredMode = RequiredMode.REQUIRED, example = "30000")
            BigDecimal amount
    ) {
    }
}