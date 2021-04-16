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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductStatusList extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private int type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private BigDecimal price;

    @OneToMany(mappedBy = "productStatusList", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductStatusDetail> productStatusDetails = new HashSet(0);

    @ManyToOne
    @JoinColumn(name="vat_id")
    private Vat vat;
}
