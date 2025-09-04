package kr.hhplus.be.server.infrastructure.message;

import java.time.LocalDateTime;

public record CouponIssueMessage(
    long userId,
    int policyId,
    LocalDateTime requestedAt,
    long sequenceNumber
) {
}
