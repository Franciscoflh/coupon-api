package com.coupon.couponapi.api.dto;

import com.coupon.couponapi.domain.Coupon;
import com.coupon.couponapi.domain.CouponStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
public class CouponResponse {

    private UUID id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDateTime expirationDate;
    private CouponStatus status;
    private boolean published;
    private boolean redeemed;

    public static CouponResponse fromEntity(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        response.id = coupon.getId();
        response.code = coupon.getCode();
        response.description = coupon.getDescription();
        response.discountValue = coupon.getDiscountValue();
        response.expirationDate = coupon.getExpirationDate();
        response.status = coupon.getStatus();
        response.published = coupon.isPublished();
        response.redeemed = coupon.isRedeemed();
        return response;
    }
}
