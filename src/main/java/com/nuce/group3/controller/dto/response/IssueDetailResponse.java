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
public class IssueDetailResponse {
    private String issueCode;
    private String productName;
    private String imei;
    private BigDecimal price;
}
