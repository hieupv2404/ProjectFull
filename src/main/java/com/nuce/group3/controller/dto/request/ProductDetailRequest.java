package com.nuce.group3.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRequest {
    private Integer productId;
    private Integer supplierId;
    @NotBlank(message = "Required Imei")
    private String imei;
    private Integer shelfId;
}
