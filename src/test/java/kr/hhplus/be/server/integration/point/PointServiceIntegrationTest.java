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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    
    @Autowired
    private EntityManager entityManager;

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
        final int threadCount = 10;

        pointService.chargePoint(userId, chargeAmount);

        // when - 10개 스레드가 동시에 포인트 사용
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    pointService.usePoint(userId, useAmount);
                    successCount.incrementAndGet();
                    System.out.println("포인트 사용 성공");
                } catch (ObjectOptimisticLockingFailureException e) {
                    failureCount.incrementAndGet();
                    System.out.println("낙관적 락 충돌 발생: " + e.getMessage());
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("기타 예외 발생: " + e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();

        // then
        var result = pointJpaRepository.findByUserId(userId);
        assertThat(result).isPresent();
        
        System.out.println("실행 시간: " + (endTime - startTime) + "ms");
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failureCount.get());
        System.out.println("최종 포인트: " + result.get().getCurrentPoint());
        
        // 낙관적 락 - 일부만 성공 (전체 포인트 - 성공 포인트)
        assertThat(result.get().getCurrentPoint()).isEqualTo(1000 - (useAmount * successCount.get()));
    }
}
