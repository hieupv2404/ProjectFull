package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusListRequest {
    @NotNull(message = "Required Vat")
    private int vatId;
    @NotNull(message = "Required User")
    private int userId;
    @NotNull(message = "Required Type")
    private int type;
}
