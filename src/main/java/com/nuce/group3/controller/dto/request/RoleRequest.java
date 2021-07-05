package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Data
public class RoleRequest {
    @NotBlank(message = Constant.REQUIRE_NAME)
    @Column(name = "role_name", unique = true)
    private String name;
    private String description;
}
