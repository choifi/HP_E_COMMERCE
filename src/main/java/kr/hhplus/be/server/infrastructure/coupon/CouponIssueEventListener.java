package kr.hhplus.be.server.infrastructure.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.common.constant.Topics;
import kr.hhplus.be.server.infrastructure.message.CouponIssueMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueEventListener {

    private final ObjectMapper objectMapper;
    private final CouponService couponService;

    @KafkaListener(topics = Topics.COUPON_ISSUE_REQUEST, groupId = Topics.COUPON_ISSUE_GROUP)
    public void handleCouponIssueRequest(String message) {
        try {
            CouponIssueMessage couponIssueMessage = objectMapper.readValue(message, CouponIssueMessage.class);
            
            // 쿠폰 발급 처리
            couponService.processCouponIssue(couponIssueMessage.userId(), couponIssueMessage.policyId());
            
        } catch (Exception e) {
        }
    }
}
