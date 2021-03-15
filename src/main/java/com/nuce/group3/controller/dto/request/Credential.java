package com.nuce.group3.controller.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Credential {
    @NotBlank(message = "Không được để trống tài khoản")
    private String userName;
    @NotBlank(message = "Không được để trống mật khẩu")
    private String passWord;
}
