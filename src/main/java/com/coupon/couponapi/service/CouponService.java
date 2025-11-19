package com.coupon.coupon_api.service;

import com.coupon.coupon_api.api.dto.CouponRequest;
import com.coupon.coupon_api.domain.Coupon;
import com.coupon.coupon_api.domain.CouponRepository;
import com.coupon.coupon_api.domain.CouponStatus;
import com.coupon.coupon_api.service.exception.BusinessException;
import com.coupon.coupon_api.service.exception.CouponNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CouponService {

    private final CouponRepository repository;

    public CouponService(CouponRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Coupon create(CouponRequest request) {
        String sanitizedCode = sanitizeCode(request.getCode());
        if (sanitizedCode.length() < 6) {
            throw new BusinessException("Code must contain at least 6 alphanumeric characters");
        }
        sanitizedCode = sanitizedCode.substring(0, 6);

        LocalDateTime now = LocalDateTime.now();
        if (request.getExpirationDate().isBefore(now)) {
            throw new BusinessException("Expiration date cannot be in the past");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(sanitizedCode);
        coupon.setDescription(request.getDescription());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setExpirationDate(request.getExpirationDate());
        coupon.setPublished(Boolean.TRUE.equals(request.getPublished()));
        coupon.setRedeemed(false);
        coupon.setDeleted(false);
        coupon.setStatus(CouponStatus.ACTIVE);

        return repository.save(coupon);
    }

    @Transactional(readOnly = true)
    public Coupon getById(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
    }

    @Transactional
    public void delete(UUID id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (coupon.isDeleted()) {
            throw new BusinessException("Coupon already deleted");
        }

        coupon.setDeleted(true);
        coupon.setStatus(CouponStatus.DELETED);
        repository.save(coupon);
    }

    private String sanitizeCode(String code) {
        if (code == null) {
            return "";
        }
        return code.replaceAll("[^A-Za-z0-9]", "");
    }
}
