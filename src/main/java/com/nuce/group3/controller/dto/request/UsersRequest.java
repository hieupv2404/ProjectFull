package com.nuce.group3.controller.dto.request;

import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersRequest {
    @NotBlank(message = "Required Username")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = Constant.UNAVAILABLE_NAME)
    private String userName;

    @Pattern(regexp = "^(.+)@(.+)\\.(.+)$", message = "Wrong Format Email")
    private String email;

    @NotBlank(message = Constant.REQUIRE_NAME)
    @Pattern(regexp = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêếìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹý ]+$",
            message = Constant.UNAVAILABLE_NAME)
    private String name;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Wrong Format Number Phone")
    private String phone;

    private Set<Integer> roles = new HashSet<>();
    @NotNull(message = "Required Branch")
    private int branchId;

}
