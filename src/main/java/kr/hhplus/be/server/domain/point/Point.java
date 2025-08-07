package kr.hhplus.be.server.domain.point;

import java.time.LocalDateTime;

public class Point {
    private int pointId;
    private int userId;
    private int currentPoint;
    private int version; // 낙관적 락을 위한 버전 필드
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Point() {}

    public Point(int userId, int currentPoint) {
        validatePoint(userId, currentPoint);
        this.userId = userId;
        this.currentPoint = currentPoint;
        this.version = 0;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    // 비즈니스 규칙 검증
    private void validatePoint(int userId, int currentPoint) {
        if (userId <= 0) {
            throw new IllegalArgumentException("사용자 ID는 1 이상이어야 합니다.");
        }
        if (currentPoint < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
    }

    // 포인트 충전
    public void charge(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }
        
        // 최대 한도 체크
        if (this.currentPoint + amount > 1_000_000) {
            throw new IllegalStateException("포인트 최대 한도를 초과합니다. 최대: 1,000,000원");
        }
        
        this.currentPoint += amount;
        this.updatedTime = LocalDateTime.now();
    }

    // 포인트 사용
    public void use(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다.");
        }
        if (this.currentPoint < amount) {
            throw new IllegalStateException("포인트가 부족합니다. 현재: " + this.currentPoint + ", 필요: " + amount);
        }
        
        this.currentPoint -= amount;
        this.updatedTime = LocalDateTime.now();
    }

    // 잔액 확인
    public boolean hasEnoughPoint(int amount) {
        return this.currentPoint >= amount;
    }

    // JPA 도메인 변환
    public void setPointId(int pointId) { this.pointId = pointId; }
    public void setCurrentPoint(int currentPoint) { this.currentPoint = currentPoint; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    // Getter
    public int getPointId() { return pointId; }
    public int getUserId() { return userId; }
    public int getCurrentPoint() { return currentPoint; }
    
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
} 