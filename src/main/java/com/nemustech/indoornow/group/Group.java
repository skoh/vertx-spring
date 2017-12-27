package com.nemustech.indoornow.group;

import org.mybatisorm.annotation.Column;
import org.mybatisorm.annotation.Table;

import com.nemustech.common.model.Default;
import com.nemustech.indoornow.user.User;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Table("V3_GROUP")
public class Group extends Default {
	@Column(primaryKey = true, autoIncrement = true)
	protected Long id;

	@Column
	protected String name;

	@Column
	protected Long parent_group_id;

	protected Object[] users;
}
