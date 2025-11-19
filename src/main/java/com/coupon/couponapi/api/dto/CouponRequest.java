package com.coupon.coupon_api.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class CouponRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.5", inclusive = true, message = "discountValue must be >= 0.5")
    private BigDecimal discountValue;

    @NotNull
    private LocalDateTime expirationDate;

    private Boolean published = Boolean.FALSE;
}
