package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.domain.point.PointHistory;

import java.time.LocalDateTime;

// 포인트 내역 응답 DTO
public class PointHistoryResponse {
    private final int historyId;
    private final int userId;
    private final int pointId;
    private final int pointAmount;
    private final String type;
    private final LocalDateTime createdTime;
    private final LocalDateTime updatedTime;

    public PointHistoryResponse(int historyId, int userId, int pointId, int pointAmount,
                               String type, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.historyId = historyId;
        this.userId = userId;
        this.pointId = pointId;
        this.pointAmount = pointAmount;
        this.type = type;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // 도메인 객체에서 DTO 변환    
    public static PointHistoryResponse from(PointHistory history) {
        return new PointHistoryResponse(
            history.getHistoryId(),
            history.getUserId(),
            history.getPointId(),
            history.getPointAmount(),
            history.getType().getDescription(),
            history.getCreatedTime(),
            history.getUpdatedTime()
        );
    }

    // Getter
    public int getHistoryId() { return historyId; }
    public int getUserId() { return userId; }
    public int getPointId() { return pointId; }
    public int getPointAmount() { return pointAmount; }
    public String getType() { return type; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
} 