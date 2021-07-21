package com.nuce.group3.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRequest {
    @NotNull(message = "Required Product")
    private Integer productId;
    @NotNull(message = "Required Product Status List")
    private Integer productStatusListId;
    @NotBlank(message = "Required Imei")
    private String imei;
    @NotNull(message = "Required Shelf")
    private Integer shelfId;
}
