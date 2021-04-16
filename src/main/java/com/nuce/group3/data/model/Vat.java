package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vat extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String tax;

    @OneToMany(mappedBy = "vat", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<VatDetail> vatDetails = new HashSet(0);

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private BigDecimal percent;
    private BigDecimal price;
    private BigDecimal total;

    @OneToMany(mappedBy = "vat", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductStatusList> productStatusLists = new HashSet(0);

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
