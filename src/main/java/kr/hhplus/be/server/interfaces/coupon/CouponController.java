package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.coupon.CouponService.CouponIssueResult;
import kr.hhplus.be.server.domain.coupon.Coupon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }


    @PostMapping("/request")
    public ResponseEntity<CouponResponse> requestCoupon(@RequestBody IssueCouponRequest request) {
        CouponIssueResult result = couponService.requestCoupon(request.getUserId(), request.getPolicyId());
        
        switch (result) {
            case SUCCESS:
                return ResponseEntity.ok(new CouponResponse("쿠폰 발급 요청이 성공했습니다."));
            case SOLD_OUT:
                return ResponseEntity.status(HttpStatus.GONE).body(new CouponResponse("쿠폰이 소진되었습니다."));
            case ALREADY_ISSUED:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new CouponResponse("이미 발급받은 쿠폰입니다."));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CouponResponse("알 수 없는 오류가 발생했습니다."));
        }
    }

    @PostMapping("/issue")
    public ResponseEntity<CouponResponse> issueCoupon(@RequestBody IssueCouponRequest request) {
        Coupon coupon = couponService.issueCoupon(request.getUserId(), request.getPolicyId());
        return ResponseEntity.ok(CouponResponse.from(coupon));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CouponResponse>> getUserCoupons(@PathVariable int userId) {
        List<Coupon> coupons = couponService.getUserCoupons(userId);
        List<CouponResponse> responses = coupons.stream()
            .map(CouponResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{couponId}/use")
    public ResponseEntity<Void> useCoupon(@PathVariable int couponId) {
        couponService.useCoupon(couponId);
        return ResponseEntity.ok().build();
    }
} 