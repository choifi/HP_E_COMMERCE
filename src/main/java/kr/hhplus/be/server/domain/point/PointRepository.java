package kr.hhplus.be.server.domain.point;

import java.util.List;
import java.util.Optional;

public interface PointRepository {
    
    // 포인트 조회(사용자 ID)
    Optional<Point> findByUserId(int userId);
    
    // 포인트 저장
    Point save(Point point);
    
    // 포인트 내역 저장
    PointHistory saveHistory(PointHistory pointHistory);
    
    // 포인트 내역 조회(사용자 ID)
    List<PointHistory> findHistoryByUserId(int userId);
} 