package kr.hhplus.be.server.infrastructure.message;

import kr.hhplus.be.server.application.data.DataPlatformService;
import kr.hhplus.be.server.common.constant.Topics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaTest {

    @InjectMocks
    private DataPlatformService dataPlatformService;

    @Mock
    private MessageClient messageClient;

    @Test
    void 카프카_메시지_발행() {
        long orderId = 123L;

        dataPlatformService.sendOrderData(orderId);

        ArgumentCaptor<OrderDataMessage> captor = ArgumentCaptor.forClass(OrderDataMessage.class);
        verify(messageClient).send(eq(Topics.ORDER_COMPLETED), eq("123"), captor.capture());
        
        assertThat(captor.getValue().orderId()).isEqualTo(123L);
    }
}
