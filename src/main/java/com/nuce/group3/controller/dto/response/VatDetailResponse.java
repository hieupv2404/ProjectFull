package com.nuce.group3.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VatDetailResponse {
    private String vatCode;
    private String productInfo;
    private String qty;
    private String priceOne;
    private BigDecimal percent;
    private BigDecimal price;
    private BigDecimal total;
    private Date createDate;
    private Date updateDate;
}
