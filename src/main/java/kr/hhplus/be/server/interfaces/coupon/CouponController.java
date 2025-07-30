package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.Coupon;
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