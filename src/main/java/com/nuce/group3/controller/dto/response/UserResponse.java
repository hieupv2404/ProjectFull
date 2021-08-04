package com.nuce.group3.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String userName;
    private String email;
    private String name;
    private String phone;
    private String password;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    private Set<String> roleName = new HashSet<>();
    private String branchName;
    private int branchId;
    private List<Integer> roleIds;
//    private Integer roleId;
}
