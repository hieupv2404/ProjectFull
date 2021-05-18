package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "Required Quantity")
    @Min(value = 0, message = "Must be Greater or Equal 0")
    private int qty;
    @Column(name = "price_one")
    @NotNull(message = "Required Price")
    @Min(value = 0, message = "Must be Greater or Equal 0")
    private BigDecimal priceOne;
    @Column(name = "active_flag")
    private int activeFlag;

    @Column(name = "qty_rest")
    private int qtyRest;


}
