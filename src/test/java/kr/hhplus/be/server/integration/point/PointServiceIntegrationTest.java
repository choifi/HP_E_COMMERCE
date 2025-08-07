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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @Test
    void 포인트_정확성_동시성_테스트() throws InterruptedException {
        // given - 포인트 1000원 충전
        final int userId = 1;
        final int chargeAmount = 1000;
        final int useAmount = 100;

        pointService.chargePoint(userId, chargeAmount);

        // when - 10명이 동시에 100원씩 사용
        int count = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(count);

        ExecutorService executor = Executors.newFixedThreadPool(count);

        for (int i = 0; i < count; i++) {
            final int id = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    pointService.usePoint(userId, useAmount);
                } catch (Exception e) {
                    System.out.println( id + " 포인트 사용 실패: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        // then - 정확히 0원이 남아야 한다
        Point finalPoint = pointService.getPointByUserId(userId);

        System.out.println("초기 포인트: " + chargeAmount);
        System.out.println("최종 포인트: " + finalPoint.getCurrentPoint());

        assertThat(finalPoint.getCurrentPoint()).isEqualTo(0);
    }
}
