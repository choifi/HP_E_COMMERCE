package kr.hhplus.be.server.infrastructure.dataplatform;

import kr.hhplus.be.server.application.data.DataPlatformService;
import kr.hhplus.be.server.domain.order.OrderCompletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataOrderEventListenerTest {

    @Mock
    private DataPlatformService dataPlatformService;

    @InjectMocks
    private DataOrderEventListener dataOrderEventListener;

    @Test
    void 주문완료시_카프카_메시지_발행() {
        OrderCompletedEvent.Completed event = new OrderCompletedEvent.Completed(123L);

        dataOrderEventListener.listen(event);

        verify(dataPlatformService).sendOrderData(123L);
    }
}
