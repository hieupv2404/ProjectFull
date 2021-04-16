package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @NotBlank(message = Constant.REQUIRE_NAME)
    @Pattern(regexp = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêếìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹý0-9 ]+$",
            message = Constant.UNAVAILABLE_NAME)
    private String name;
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Wrong Format Number Phone")
    private String phone;
    private String address;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Vat> vats = new HashSet(0);
}
