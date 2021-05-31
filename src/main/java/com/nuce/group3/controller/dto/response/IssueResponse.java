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
public class IssueResponse {
    private String userName;
    private String customerName;
    private String code;
    private BigDecimal price;
    private Date createDate;
    private Date updateDate;
}
