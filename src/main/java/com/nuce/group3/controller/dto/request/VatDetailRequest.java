package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VatDetailRequest {
    private int vatId;
    private int productId;
    private int qty;
    private BigDecimal priceOne;
}
