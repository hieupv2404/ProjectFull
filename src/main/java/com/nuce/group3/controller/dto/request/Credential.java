package com.nuce.group3.controller.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Credential {
    @NotBlank(message = "Trong username")
    private String userName;
    @NotBlank(message = "Thieu Pass")
    private String passWord;
}
