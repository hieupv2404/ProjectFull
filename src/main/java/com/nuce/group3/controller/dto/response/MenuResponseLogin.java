package com.nuce.group3.controller.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MenuResponseLogin {
    private int parentId;
    private String url;
    private String name;
    private int orderIndex;
}
