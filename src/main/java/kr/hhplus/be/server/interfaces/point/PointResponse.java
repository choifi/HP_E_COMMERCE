package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.domain.point.Point;

import java.time.LocalDateTime;

// 포인트 응답 DTO
public class PointResponse {
    private final int pointId;
    private final int userId;
    private final int currentPoint;
    private final LocalDateTime createdTime;
    private final LocalDateTime updatedTime;

    public PointResponse(int pointId, int userId, int currentPoint, 
                        LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.pointId = pointId;
        this.userId = userId;
        this.currentPoint = currentPoint;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // 도메인 객체에서 DTO 변환
    public static PointResponse from(Point point) {
        return new PointResponse(
            point.getPointId(),
            point.getUserId(),
            point.getCurrentPoint(),
            point.getCreatedTime(),
            point.getUpdatedTime()
        );
    }

    // Getter
    public int getPointId() { return pointId; }
    public int getUserId() { return userId; }
    public int getCurrentPoint() { return currentPoint; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
} 