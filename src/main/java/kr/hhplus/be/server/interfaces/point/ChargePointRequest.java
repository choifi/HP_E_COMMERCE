package kr.hhplus.be.server.interfaces.point;

// 포인트 충전 요청 DTO
public class ChargePointRequest {
    private final int userId;
    private final int amount;

    public ChargePointRequest(int userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getter
    public int getUserId() { return userId; }
    public int getAmount() { return amount; }
} 