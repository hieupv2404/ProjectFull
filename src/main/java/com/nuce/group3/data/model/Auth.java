package com.nuce.group3.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Auth implements java.io.Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Menu menu;
	private Role role;
	private int permission;
	@Column(name = "active_flag")
	private int activeFlag;
	@Column(name = "create_date")
	@CreatedDate
	private Date createDate;
	@Column(name = "update_date")
	@LastModifiedDate
	private Date updateDate;


}
