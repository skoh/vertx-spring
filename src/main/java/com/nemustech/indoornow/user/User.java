package com.nemustech.indoornow.user;

import org.mybatisorm.annotation.Column;
import org.mybatisorm.annotation.Table;

import com.nemustech.common.model.Default;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Table("V3_USER")
public class User extends Default {
	@Column(primaryKey = true, autoIncrement = true)
	protected Long id;

	@Column
	protected String cmp_no;

	@Column
	protected String name;

	@Column
	protected String title;

	@Column
	protected String hp;

	@Column
	protected String email;

	@Column
	protected Long group_id;
}
