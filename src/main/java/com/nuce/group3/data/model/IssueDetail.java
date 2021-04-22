package com.nuce.group3.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class IssueDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Required Imei")
    private String imei;
    @Column(name = "active_flag")
    private int activeFlag;
    @Column(name = "price_one")
    private BigDecimal priceOne;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductInfo productInfo;
}
