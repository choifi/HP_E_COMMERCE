package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    // 포인트 충전
    @PostMapping("/charge")
    public ResponseEntity<PointResponse> chargePoint(@RequestBody ChargePointRequest request) {
        Point point = pointService.chargePoint(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(PointResponse.from(point));
    }

    // 포인트 사용
    @PostMapping("/use")
    public ResponseEntity<PointResponse> usePoint(@RequestBody UsePointRequest request) {
        Point point = pointService.usePoint(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(PointResponse.from(point));
    }

    // 포인트 잔액 조회
    @GetMapping("/{userId}")
    public ResponseEntity<PointResponse> getPointByUserId(@PathVariable int userId) {
        Point point = pointService.getPointByUserId(userId);
        return ResponseEntity.ok(PointResponse.from(point));
    }

    //포인트 내역 조회
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<PointHistoryResponse>> getPointHistoryByUserId(@PathVariable int userId) {
        List<PointHistory> histories = pointService.getPointHistoryByUserId(userId);
        List<PointHistoryResponse> responses = histories.stream()
            .map(PointHistoryResponse::from)
            .toList();
        
        return ResponseEntity.ok(responses);
    }
} 