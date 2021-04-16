package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VatDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "vat_id")
    private Vat vat;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductInfo productInfo;
    private int qty;
    private BigDecimal priceOne;
    private BigDecimal priceTotal;
    @Column(name = "active_flag")
    private int activeFlag;

}
