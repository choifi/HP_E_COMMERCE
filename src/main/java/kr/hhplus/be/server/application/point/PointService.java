package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryType;
import kr.hhplus.be.server.domain.point.PointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    // 포인트 충전
    @Transactional
    public Point chargePoint(int userId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전할 금액은 0보다 커야 합니다.");
        }

        // 기존 포인트 조회 또는 새로 생성
        Point point = pointRepository.findByUserId(userId)
            .orElse(new Point(userId, 0));

        // 포인트 충전
        point.charge(amount);

        // 포인트 저장
        Point savedPoint = pointRepository.save(point);

        // 포인트 내역 저장
        PointHistory history = new PointHistory(userId, savedPoint.getPointId(), amount, PointHistoryType.CHARGE);
        pointRepository.saveHistory(history);

        return savedPoint;
    }

    // 포인트 사용
    @Transactional
    public Point usePoint(int userId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용할 금액은 0보다 커야 합니다.");
        }

        // 포인트 조회
        Point point = pointRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다. 사용자 ID: " + userId));

        // 포인트 사용
        point.use(amount);

        // 포인트 저장
        Point savedPoint = pointRepository.save(point);

        // 포인트 내역 저장
        PointHistory history = new PointHistory(userId, savedPoint.getPointId(), amount, PointHistoryType.USE);
        pointRepository.saveHistory(history);

        return savedPoint;
    }

    // 포인트 잔액 조회
    public Point getPointByUserId(int userId) {
    
        return pointRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다. 사용자 ID: " + userId));
    }

    // 
    public List<PointHistory> getPointHistoryByUserId(int userId) {
        
        return pointRepository.findHistoryByUserId(userId);
    }

    // 포인트 잔액 확인
    public boolean hasEnoughPoint(int userId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("확인할 금액은 0보다 커야 합니다.");
        }

        return pointRepository.findByUserId(userId)
            .map(point -> point.hasEnoughPoint(amount))
            .orElse(false);
    }
} 