package kr.hhplus.be.server.infrastructure.message;

import java.time.LocalDateTime;

public record OrderDataMessage(
    long orderId,
    LocalDateTime completedAt
) {
}
