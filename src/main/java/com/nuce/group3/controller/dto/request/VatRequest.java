package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class VatRequest {
    @NotBlank(message = Constant.REQUIRE_CODE)
    private String code;
    private String tax;
    private int supplierId;
    private BigDecimal percent;
}
