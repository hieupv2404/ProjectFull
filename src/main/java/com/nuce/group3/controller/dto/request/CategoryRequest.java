package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryRequest {
    @NotBlank(message = Constant.REQUIRE_NAME)
    private String name;
    @NotBlank(message = Constant.REQUIRE_CODE)
    private String code;
    private String description;
}
