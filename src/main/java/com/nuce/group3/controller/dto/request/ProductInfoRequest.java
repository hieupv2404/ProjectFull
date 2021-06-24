package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ProductInfoRequest implements Serializable {
    @NotBlank(message = Constant.REQUIRE_NAME)
    private String name;
    private String description;
    private String imgUrl;
    @NotNull(message = "Required Category")
    private Integer categoryId;
}
