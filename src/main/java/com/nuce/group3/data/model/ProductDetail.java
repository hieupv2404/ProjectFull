package com.nuce.group3.data.model;

import com.nuce.group3.enums.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductDetail extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotBlank(message = "Required Imei")
    @Pattern(regexp = "[^A-Za-z0-9]+", message = "Wrong Format Imei")
    private String imei;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductInfo productInfo;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    @Enumerated(value = EnumType.STRING)
    private EnumStatus status;

}
