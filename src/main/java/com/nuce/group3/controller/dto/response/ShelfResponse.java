package com.nuce.group3.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
@Builder
public class ShelfResponse {
    private int id;
    private String name;
    private String description;
    private Date createDate;
    private Date updateDate;
    private int total;
    private int qty;
    private int qtyRest;
    private String branchName;

}
