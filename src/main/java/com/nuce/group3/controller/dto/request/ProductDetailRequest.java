package com.nuce.group3.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRequest {
    @NotBlank(message = "Required Product Name")
    private Integer productId;
    @NotBlank(message = "Required Product Status Code")
    private Integer productStatusListId;
    @NotBlank(message = "Required Imei")
    private String imei;
    private Integer shelfId;
}
