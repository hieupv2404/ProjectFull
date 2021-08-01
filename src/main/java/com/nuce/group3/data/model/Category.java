package com.nuce.group3.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    private String description;
    @Column(name = "active_flag")
    private int activeFlag;
    @Column(name = "create_date")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @Column(name = "update_date")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductInfo> productInfos = new HashSet(0);

}
