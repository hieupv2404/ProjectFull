package com.nuce.group3.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductStatusDetailResponse {
    private int id;
    private String productStatusListCode;
    private String productInfo;
    private int qty;
    private BigDecimal priceOne;
    private BigDecimal priceTotal;
    private int qtyRest;
}
