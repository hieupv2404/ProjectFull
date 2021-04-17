package com.nuce.group3.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRequest {
    private Integer productId;
    private Integer supplierId;
    private String imei;
    private Integer shelfId;
}
