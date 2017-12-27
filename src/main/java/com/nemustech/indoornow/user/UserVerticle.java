package com.nemustech.indoornow.user;

import org.springframework.context.ApplicationContext;

import com.nemustech.indoornow.common.AbstractCRUDVerticle;

/**
 * 사용자 Verticle
 */
public class UserVerticle extends AbstractCRUDVerticle<User> {
	public UserVerticle(ApplicationContext context) {
		super(context);

		service = context.getBean(UserService.class);
	}

	@Override
	public String getCacheName() {
		return "user";
	}
}
