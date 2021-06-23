package com.nuce.group3.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VatDetailResponse {
    private int id;
    private String vatCode;
    private String productInfo;
    private int qty;
    private BigDecimal priceOne;
    private BigDecimal priceTotal;
}
