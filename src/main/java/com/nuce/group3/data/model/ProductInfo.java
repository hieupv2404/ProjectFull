package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductInfo extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cate_id")
    private Category category;
    @NotBlank(message = Constant.REQUIRE_NAME)
    private String name;
    private String description;
    @Column(name = "img_name")
    private String imgName;

    @Column(name = "img_url")
    private String imgUrl;

    //    private MultipartFile multipartFile;
//    private Integer cateId;
    private int qty;
    @Column(name = "price_in")
    private BigDecimal priceIn;

    @Column(name = "price_out")
    private BigDecimal priceOut;

    @OneToMany(mappedBy = "productInfo", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductStatusDetail> productStatusDetails = new HashSet<>(0);

    @OneToMany(mappedBy = "productInfo", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<IssueDetail> issueDetails = new HashSet<>(0);
}
