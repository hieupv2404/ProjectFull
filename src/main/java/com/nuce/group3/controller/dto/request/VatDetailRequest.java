package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VatDetailRequest {
    @NotNull(message = "Required Vat")
    private int vatId;
    @NotNull(message = "Required Product")
    private int productId;
    @NotNull(message = "Required Quantity")
    @Min(value = 0, message = "Must be Greater or Equal 0")
    private int qty;
    @NotNull(message = "Required Price")
    @Min(value = 0, message = "Must be Greater or Equal 0")
    private BigDecimal priceOne;
}
