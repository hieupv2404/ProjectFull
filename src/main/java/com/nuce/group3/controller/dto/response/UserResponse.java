package com.nuce.group3.controller.dto.response;

import com.nuce.group3.data.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserResponse {
    private String userName;
    private String email;
    private String name;
    private Date createDate;
    private Date updateDate;
    private Set<String> roleName = new HashSet<>();
//    private Integer roleId;
}
