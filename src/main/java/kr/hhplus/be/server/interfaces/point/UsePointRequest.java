package kr.hhplus.be.server.interfaces.point;

// 포인트 사용 요청 DTO
public class UsePointRequest {
    private final int userId;
    private final int amount;

    public UsePointRequest(int userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getter
    public int getUserId() { return userId; }
    public int getAmount() { return amount; }
}