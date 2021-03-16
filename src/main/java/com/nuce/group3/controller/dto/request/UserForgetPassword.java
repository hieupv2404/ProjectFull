package com.nuce.group3.controller.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserForgetPassword {
    @NotBlank(message = "Required mail")
    @Pattern(regexp = "^(.+)@(.+)\\.(.+)$", message = "Invalid format Email")
    private String email;
}
