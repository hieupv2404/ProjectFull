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
public class ProductStatusListResponse {
    private String code;
    private String vatCode;
    private String userName;
    private BigDecimal price;
    private Date createDate;
    private Date updateDate;
}
