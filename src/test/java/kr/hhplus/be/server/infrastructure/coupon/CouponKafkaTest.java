package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.application.coupon.CouponIssueEventPublisher;
import kr.hhplus.be.server.common.constant.Topics;
import kr.hhplus.be.server.infrastructure.message.CouponIssueMessage;
import kr.hhplus.be.server.infrastructure.message.MessageClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponKafkaTest {

    @InjectMocks
    private CouponIssueEventPublisher couponIssueEventPublisher;

    @Mock
    private MessageClient messageClient;

    @Test
    void 쿠폰_발급_요청_카프카_메시지_발행() {

        long userId = 123L;
        int policyId = 456;
        long sequenceNumber = 1640995200000L;

        couponIssueEventPublisher.publishCouponIssueRequest(userId, policyId, sequenceNumber);

        ArgumentCaptor<CouponIssueMessage> captor = ArgumentCaptor.forClass(CouponIssueMessage.class);
        verify(messageClient).send(eq(Topics.COUPON_ISSUE_REQUEST), eq("1640995200000"), captor.capture());
        
        CouponIssueMessage message = captor.getValue();
        assertThat(message.userId()).isEqualTo(123L);
        assertThat(message.policyId()).isEqualTo(456);
        assertThat(message.sequenceNumber()).isEqualTo(1640995200000L);
        assertThat(message.requestedAt()).isNotNull();
    }

    @Test
    void 쿠폰_발급_요청_순서_보장_테스트() {
        // Given
        long userId = 123L;
        int policyId = 456;
        
        // 여러 메시지를 순차적으로 발행
        for (int i = 0; i < 3; i++) {
            long sequenceNumber = System.currentTimeMillis() + i;
            couponIssueEventPublisher.publishCouponIssueRequest(userId, policyId, sequenceNumber);
        }

        // 3번의 메시지 발행이 호출 확인
        verify(messageClient, org.mockito.Mockito.times(3)).send(
            eq(Topics.COUPON_ISSUE_REQUEST), 
            org.mockito.ArgumentMatchers.anyString(), 
            org.mockito.ArgumentMatchers.any(CouponIssueMessage.class)
        );
    }

    @Test
    void 쿠폰_발급_요청_파라미터_검증() {

        long userId = 999L;
        int policyId = 888;
        long sequenceNumber = 1234567890L;

        couponIssueEventPublisher.publishCouponIssueRequest(userId, policyId, sequenceNumber);

        ArgumentCaptor<CouponIssueMessage> captor = ArgumentCaptor.forClass(CouponIssueMessage.class);
        verify(messageClient).send(eq(Topics.COUPON_ISSUE_REQUEST), eq("1234567890"), captor.capture());
        
        CouponIssueMessage message = captor.getValue();
        assertThat(message.userId()).isEqualTo(999L);
        assertThat(message.policyId()).isEqualTo(888);
        assertThat(message.sequenceNumber()).isEqualTo(1234567890L);
        assertThat(message.requestedAt()).isInstanceOf(LocalDateTime.class);
    }
}
