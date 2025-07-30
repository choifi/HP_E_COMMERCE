package kr.hhplus.be.server.domain.point;

// 포인트 내역 타입
public enum PointHistoryType {
    CHARGE("충전"),
    USE("사용"),
    REFUND("환불");

    private final String description;

    PointHistoryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 