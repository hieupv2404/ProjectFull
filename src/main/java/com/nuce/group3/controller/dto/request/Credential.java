package com.nuce.group3.controller.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Credential {
    @NotBlank(message = "Required username")
    private String userName;
    @NotBlank(message = "Required password")
    private String passWord;
}
