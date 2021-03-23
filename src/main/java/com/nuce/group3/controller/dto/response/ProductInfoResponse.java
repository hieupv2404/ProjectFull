package com.nuce.group3.controller.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Date;


@Data
@Builder
public class ProductInfoResponse {
    private String name;
    private String description;
    private String imgUrl;
    private String categoryName;
    private int qty;
    private Date createDate;
    private Date updateDate;

    public String getImgPath() {
        if (imgUrl == null) return null;
        return "/product-info-img/" + imgUrl;
    }
}
