package com.nuce.group3.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nuce.group3.enums.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private int id;
    private String productName;
    private String supplierName;
    private String categoryName;
    private String productStatusListCode;
    private String imei;
    @Enumerated(value = EnumType.STRING)
    private EnumStatus status;
    private String shelfName;
    private BigDecimal priceIn;
    private BigDecimal priceOut;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    private int productInfoId;
    private int productStatusId;
    private int shelfId;
    private String imgUrl;
}
