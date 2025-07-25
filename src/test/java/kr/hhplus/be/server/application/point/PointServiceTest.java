package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryType;
import kr.hhplus.be.server.domain.point.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    private PointService pointService;

    @BeforeEach
    void setUp() {
        pointService = new PointService(pointRepository);
    }

    @Test
    void 포인트_충전_성공() {
        int userId = 1;
        int chargeAmount = 1000;
        Point point = new Point(userId, 0);
        point.setPointId(1);
        point.setCreatedTime(LocalDateTime.now());
        point.setUpdatedTime(LocalDateTime.now());

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(pointRepository.save(any(Point.class))).thenReturn(point);
        when(pointRepository.saveHistory(any(PointHistory.class))).thenReturn(new PointHistory(userId, 1, chargeAmount, PointHistoryType.CHARGE));

        Point result = pointService.chargePoint(userId, chargeAmount);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        verify(pointRepository).findByUserId(userId);
        verify(pointRepository).save(any(Point.class));
        verify(pointRepository).saveHistory(any(PointHistory.class));
    }

    @Test
    void 포인트_사용_성공() {
        int userId = 1;
        int useAmount = 500;
        Point point = new Point(userId, 1000);
        point.setPointId(1);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointRepository.save(any(Point.class))).thenReturn(point);
        when(pointRepository.saveHistory(any(PointHistory.class))).thenReturn(new PointHistory(userId, 1, useAmount, PointHistoryType.USE));

        Point result = pointService.usePoint(userId, useAmount);

        assertThat(result).isNotNull();
        verify(pointRepository).findByUserId(userId);
        verify(pointRepository).save(any(Point.class));
        verify(pointRepository).saveHistory(any(PointHistory.class));
    }
}