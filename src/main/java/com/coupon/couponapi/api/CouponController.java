package com.coupon.couponapi.api;

import com.coupon.couponapi.api.dto.CouponRequest;
import com.coupon.couponapi.api.dto.CouponResponse;
import com.coupon.couponapi.domain.Coupon;
import com.coupon.couponapi.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService service;

    public CouponController(CouponService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CouponRequest request) {
        Coupon created = service.create(request);
        CouponResponse response = CouponResponse.fromEntity(created);
        return ResponseEntity.created(URI.create("/coupon/" + created.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getById(@PathVariable UUID id) {
        Coupon coupon = service.getById(id);
        return ResponseEntity.ok(CouponResponse.fromEntity(coupon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
