package com.nuce.group3.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoResponse {
    private String name;
    private String description;
    private String imgName;
    private String categoryName;
    private int qty;
    private BigDecimal priceIn;
    private BigDecimal priceOut;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    public String getImgPath() {
        if (imgName == null) return null;
        return Constant.UPLOAD_PATH + imgName;
    }

}
