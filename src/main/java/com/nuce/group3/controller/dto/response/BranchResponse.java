package com.nuce.group3.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class BranchResponse {
    private String name;
    private String code;
    private String phone;
    private String address;
    private Date createDate;
    private Date updateDate;

}
