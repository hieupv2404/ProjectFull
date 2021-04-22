package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuce.group3.utils.Constant;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
    @Pattern(regexp = "[^A-Za-z0-9]+", message = "Wrong Format Name")
    private String name;
    private String description;
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
