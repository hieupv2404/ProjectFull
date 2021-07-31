package com.nuce.group3.controller.dto.response;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserResponseLogin {
    private String userName;
    private String email;
    private String name;
    private Set<String> roleName = new HashSet<>();
    private Set<MenuResponseLogin> menuSet = new HashSet<>();
    private String token;
    private String branchName;
    private Integer branchId;

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
}
