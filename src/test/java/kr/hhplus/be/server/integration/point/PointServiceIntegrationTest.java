package kr.hhplus.be.server.integration.point;

import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryType;
import kr.hhplus.be.server.infrastructure.point.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;
    
    @Autowired
    private PointJpaRepository pointJpaRepository;
    
    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Test
    @Transactional
    void 포인트_충전_성공() {
        // given
        int userId = 1;
        int chargeAmount = 1000;

        // when
        Point result = pointService.chargePoint(userId, chargeAmount);

        // then
        // Service
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getCurrentPoint()).isEqualTo(chargeAmount);
        
        // PointEntity
        var pointEntity = pointJpaRepository.findByUserId(userId);
        assertThat(pointEntity).isPresent();
        assertThat(pointEntity.get().getCurrentPoint()).isEqualTo(chargeAmount);
        
        // PointHistoryEntity
        var historyEntities = pointHistoryJpaRepository.findHistoryByUserId(userId);
        assertThat(historyEntities).hasSize(1);
        assertThat(historyEntities.get(0).getType()).isEqualTo(PointHistoryType.CHARGE);
    }

    @Test
    @Transactional
    void 포인트_충전_실패_최대한도_초과() {
        // given
        int userId = 1;
        int chargeAmount = 1_000_001; 

        assertThatThrownBy(() -> pointService.chargePoint(userId, chargeAmount))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("포인트 최대 한도를 초과합니다. 최대: 1,000,000원");
    }

    @Test
    @Transactional
    void 포인트_사용_성공() {
        // given
        int userId = 1;
        int chargeAmount = 1000;
        int useAmount = 500;

        // when
        Point chargeResult = pointService.chargePoint(userId, chargeAmount);
        Point useResult = pointService.usePoint(userId, useAmount);

        // then
        assertThat(useResult.getUserId()).isEqualTo(userId);
        assertThat(useResult.getCurrentPoint()).isEqualTo(chargeAmount - useAmount);

        var historyEntities = pointHistoryJpaRepository.findHistoryByUserId(userId);
        assertThat(historyEntities).hasSize(2);
        assertThat(historyEntities.get(0).getType()).isEqualTo(PointHistoryType.USE);
        assertThat(historyEntities.get(1).getType()).isEqualTo(PointHistoryType.CHARGE);
    }

    @Test
    @Transactional
    void 포인트_사용_실패_잔액_부족() {
        // given
        int userId = 1;
        int chargeAmount = 1000;
        int useAmount = 1500;

        // when
        pointService.chargePoint(userId, chargeAmount);
        
        // then
        assertThatThrownBy(() -> pointService.usePoint(userId, useAmount))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("포인트가 부족합니다");
    }

    @Test
    @Transactional
    void 포인트_내역_조회_성공() {
        // given
        int userId = 1;
        int chargeAmount = 1000;
        int useAmount = 500;

        // when
        pointService.chargePoint(userId, chargeAmount);
        pointService.usePoint(userId, useAmount);
        List<PointHistory> histories = pointService.getPointHistoryByUserId(userId);

        // then
        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).getType()).isEqualTo(PointHistoryType.USE);
        assertThat(histories.get(1).getType()).isEqualTo(PointHistoryType.CHARGE);
    }

    @Test
    @Transactional
    void 포인트_조회_성공() {
        // given
        int userId = 1;
        int chargeAmount = 1000;

        // when
        pointService.chargePoint(userId, chargeAmount);
        Point result = pointService.getPointByUserId(userId);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getCurrentPoint()).isEqualTo(chargeAmount);
    }
}
