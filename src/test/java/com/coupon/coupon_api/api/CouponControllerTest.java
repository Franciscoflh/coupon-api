package com.coupon.coupon_api.api;

import com.coupon.couponapi.domain.Coupon;
import com.coupon.couponapi.domain.CouponRepository;
import com.coupon.couponapi.domain.CouponStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CouponRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void deveCriarCupomComCodigoSanitizadoESeisCaracteres() throws Exception {
        var body = """
                {
                  "code": "ABC-123",
                  "description": "Teste de cupom",
                  "discountValue": 1.0,
                  "expirationDate": "%s",
                  "published": true
                }
                """.formatted(LocalDateTime.now().plusDays(1).toString());

        var result = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.published").value(true))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        var jsonNode = objectMapper.readTree(responseJson);
        assertThat(jsonNode.get("code").asText()).hasSize(6);
    }

    @Test
    void naoDeveCriarCupomComDescontoMenorQueZeroPontoCinco() throws Exception {
        var body = """
                {
                  "code": "ABC-123",
                  "description": "Teste",
                  "discountValue": 0.4,
                  "expirationDate": "%s",
                  "published": false
                }
                """.formatted(LocalDateTime.now().plusDays(1).toString());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarCupomComDataExpiracaoNoPassado() throws Exception {
        var body = """
                {
                  "code": "ABC-123",
                  "description": "Teste",
                  "discountValue": 1.0,
                  "expirationDate": "%s",
                  "published": false
                }
                """.formatted(LocalDateTime.now().minusDays(1).toString());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveFazerSoftDeleteENaoPermitirDeletarNovamente() throws Exception {
        Coupon coupon = new Coupon();
        coupon.setCode("ABC123");
        coupon.setDescription("Cupom teste");
        coupon.setDiscountValue(BigDecimal.valueOf(1.0));
        coupon.setExpirationDate(LocalDateTime.now().plusDays(1));
        coupon.setPublished(false);
        coupon.setRedeemed(false);
        coupon.setDeleted(false);
        coupon.setStatus(CouponStatus.ACTIVE);
        coupon = repository.save(coupon);

        mockMvc.perform(delete("/coupon/" + coupon.getId()))
                .andExpect(status().isNoContent());

        Coupon deleted = repository.findById(coupon.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();

        mockMvc.perform(delete("/coupon/" + coupon.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveRetornarCupomDeletadoNoGet() throws Exception {
        Coupon coupon = new Coupon();
        coupon.setCode("ABC123");
        coupon.setDescription("Cupom teste");
        coupon.setDiscountValue(BigDecimal.valueOf(1.0));
        coupon.setExpirationDate(LocalDateTime.now().plusDays(1));
        coupon.setPublished(false);
        coupon.setRedeemed(false);
        coupon.setDeleted(true);
        coupon.setStatus(CouponStatus.DELETED);
        coupon = repository.save(coupon);

        mockMvc.perform(get("/coupon/" + coupon.getId()))
                .andExpect(status().isNotFound());
    }
}
