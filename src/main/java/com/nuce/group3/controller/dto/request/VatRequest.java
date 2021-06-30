package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class VatRequest {
    @NotBlank(message = Constant.REQUIRE_CODE)
    private String code;
    private String tax;
    private BigDecimal percent;
    @NotNull(message = "Required Username")
    private String userName;
    @NotNull(message = "Required Branch")
    private Integer branchId;
}
