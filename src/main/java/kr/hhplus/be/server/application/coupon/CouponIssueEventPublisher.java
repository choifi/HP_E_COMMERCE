package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.constant.Topics;
import kr.hhplus.be.server.infrastructure.message.CouponIssueMessage;
import kr.hhplus.be.server.infrastructure.message.MessageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponIssueEventPublisher {

    private final MessageClient messageClient;

    public void publishCouponIssueRequest(long userId, int policyId, long sequenceNumber) {
        CouponIssueMessage message = new CouponIssueMessage(userId, policyId, LocalDateTime.now(), sequenceNumber);
        String key = String.valueOf(sequenceNumber);
        messageClient.send(Topics.COUPON_ISSUE_REQUEST, key, message);
    }
}
