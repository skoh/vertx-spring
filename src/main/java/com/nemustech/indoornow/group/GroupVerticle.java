package com.nemustech.indoornow.group;

import org.springframework.context.ApplicationContext;

import com.nemustech.indoornow.common.AbstractCRUDVerticle;

/**
 * 부서 Verticle
 */
public class GroupVerticle extends AbstractCRUDVerticle<Group> {
	public GroupVerticle(ApplicationContext context) {
		super(context);

		service = context.getBean(GroupService.class);
	}

	@Override
	public String getCacheName() {
		return "group";
	}
}
