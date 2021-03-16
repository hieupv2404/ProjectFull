package com.nuce.group3.controller.dto.response;

import lombok.Data;

@Data
public class ProductInfoResponse {
    private String name;
    private String description;
    private String imgUrl;
    private String categoryName;

    public String getImgPath() {
        if (imgUrl == null) return null;
        return "/product-info-img/" + imgUrl;
    }
}
