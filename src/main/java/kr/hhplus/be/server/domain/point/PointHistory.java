package kr.hhplus.be.server.domain.point;

import java.time.LocalDateTime;

public class PointHistory {
    private int historyId;
    private int userId;
    private int pointId;
    private int pointAmount;
    private PointHistoryType type;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public PointHistory() {}

    public PointHistory(int userId, int pointId, int pointAmount, PointHistoryType type) {
        validatePointHistory(userId, pointId, pointAmount, type);
        this.userId = userId;
        this.pointId = pointId;
        this.pointAmount = pointAmount;
        this.type = type;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }


    private void validatePointHistory(int userId, int pointId, int pointAmount, PointHistoryType type) {
        if (pointAmount <= 0) {
            throw new IllegalArgumentException("포인트 금액은 0보다 커야 합니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("포인트 내역 타입은 필수입니다.");
        }
    }

    // JPA와 도메인 변환
    public void setHistoryId(int historyId) { this.historyId = historyId; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    // Getter 메서드
    public int getHistoryId() { return historyId; }
    public int getUserId() { return userId; }
    public int getPointId() { return pointId; }
    public int getPointAmount() { return pointAmount; }
    public PointHistoryType getType() { return type; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
} 