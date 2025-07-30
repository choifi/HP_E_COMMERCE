package kr.hhplus.be.server.infrastructure.point;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point")
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private int pointId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "current_point", nullable = false)
    private int currentPoint;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    public PointEntity() {}

    // Getter, Setter
    public int getPointId() { return pointId; }
    public void setPointId(int pointId) { this.pointId = pointId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getCurrentPoint() { return currentPoint; }
    public void setCurrentPoint(int currentPoint) { this.currentPoint = currentPoint; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    // 도메인 변환 메서드
    public kr.hhplus.be.server.domain.point.Point toDomain() {
        kr.hhplus.be.server.domain.point.Point point = 
            new kr.hhplus.be.server.domain.point.Point(userId, currentPoint);
        
        point.setPointId(pointId);
        point.setCreatedTime(createdTime);
        point.setUpdatedTime(updatedTime);
        
        return point;
    }

    // 도메인 객체에서 변환
    public static PointEntity fromDomain(kr.hhplus.be.server.domain.point.Point point) {
        PointEntity entity = new PointEntity();
        entity.setPointId(point.getPointId());
        entity.setUserId(point.getUserId());
        entity.setCurrentPoint(point.getCurrentPoint());
        entity.setCreatedTime(point.getCreatedTime());
        entity.setUpdatedTime(point.getUpdatedTime());
        
        return entity;
    }
} 