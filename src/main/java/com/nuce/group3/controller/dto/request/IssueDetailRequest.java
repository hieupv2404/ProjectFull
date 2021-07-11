package com.nuce.group3.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueDetailRequest {
    @NotNull(message = "Required Issue")
    private int issueId;
    @NotNull(message = "Required Product")
    private int productId;
    @NotNull(message = "Required Imei")
    private String imei;
}
