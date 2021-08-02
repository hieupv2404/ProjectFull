package com.nuce.group3.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassRequest {
    @NotBlank(message = "Required Password")
    private String oldPass;
    @NotBlank(message = "Required Password")
    private String newPassOnce;
    @NotBlank(message = "Required Password")
    private String newPassTwice;
}
