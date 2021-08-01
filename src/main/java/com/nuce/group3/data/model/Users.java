package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuce.group3.utils.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Required Username")
    @Column(name="user_name", unique = true)
    private String userName;

    @NotNull(message = "Required Password")
    @JsonIgnore
    private String password;

    @Pattern(regexp = "^(.+)@(.+)\\.(.+)$", message = "Wrong Format Email")
    private String email;

    @Column(name = "active_flag")
    private int activeFlag;
    @Column(name = "create_date")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @Column(name = "update_date")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    @NotBlank(message = Constant.REQUIRE_NAME)
    @Pattern(regexp = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêếìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹý ]+$",
            message = Constant.UNAVAILABLE_NAME)
    private String name;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Wrong Format Number Phone")
    private String phone;
    private int status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Vat> vats = new HashSet<>(0);

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductStatusList> productStatusLists = new HashSet<>(0);

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Issue> issues = new HashSet<>(0);

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
