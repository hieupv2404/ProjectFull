package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuce.group3.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shelf extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank(message = Constant.REQUIRE_NAME)
    private String name;
    private String description;
    private int total;
    private int qty;
    private int qtyRest;

    @OneToMany(mappedBy = "shelf", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductDetail> productDetails = new HashSet<>(0);

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;
    }
