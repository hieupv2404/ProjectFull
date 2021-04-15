package com.nuce.group3.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Supplier extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private Date createDate;
    private Date updateDate;
    private Integer activeFlag;
}
