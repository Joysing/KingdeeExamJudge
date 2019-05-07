package com.kingdee.exam.judger.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
	private Integer userId;
	private String username;
	private String password;
	private String trueName;
	private String roles;
	private String phone;
	private String code;
	private boolean enabled;
}