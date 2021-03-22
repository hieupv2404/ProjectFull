package com.nuce.group3.data.model;

import com.nuce.group3.utils.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class ProductInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cate_id")
    private Category category;
    @NotBlank(message = Constant.REQUIRE_NAME)
    private String name;
    private String description;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "active_flag")
    private int activeFlag;
    @Column(name = "create_date")
    @CreatedDate
    private Date createDate;
    @Column(name = "update_date")
    @LastModifiedDate
    private Date updateDate;
    //    private MultipartFile multipartFile;
//    private Integer cateId;
    private int qty;


}
