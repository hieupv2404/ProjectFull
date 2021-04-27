package com.nuce.group3.controller.dto.response;

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
public class VatResponse {
    private String userName;
    private String supplierName;
    private String code;
    private String tax;
    private BigDecimal percent;
    private BigDecimal price;
    private BigDecimal total;
    private Date createDate;
    private Date updateDate;
}
