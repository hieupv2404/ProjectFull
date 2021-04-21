package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ShelfRequest {
    @NotBlank(message = Constant.REQUIRE_NAME)
    @Pattern(regexp = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêếìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹý0-9 ]+$",
            message = Constant.UNAVAILABLE_NAME)
    private String name;
    private String description;
    @NotBlank(message = "Required total quantity")
    private int total;
    @NotBlank(message = "Required branch")
    private int branchId;

}
