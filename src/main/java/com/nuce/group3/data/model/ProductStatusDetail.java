package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_status_list_id")
    private ProductStatusList productStatusList;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductInfo productInfo;

    private int qty;
    private BigDecimal priceOne;
    private BigDecimal priceTotal;
    @Column(name = "active_flag")
    private int activeFlag;

    @Column(name = "qty_rest")
    private int qtyRest;


}
