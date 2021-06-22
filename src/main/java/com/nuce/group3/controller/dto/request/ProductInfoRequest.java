package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProductInfoRequest {
    @NotBlank(message = Constant.REQUIRE_NAME)
    private String name;
    private String description;
    private MultipartFile imgUrl;
    @NotNull(message = "Required Category")
    private Integer categoryId;
}
