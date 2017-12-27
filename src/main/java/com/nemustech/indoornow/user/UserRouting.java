package com.nemustech.indoornow.user;

import com.nemustech.indoornow.common.AbstractCRUDRouting;

import io.vertx.core.Vertx;

/**
 * 사용자 Routing
 */
public class UserRouting extends AbstractCRUDRouting<User> {
	public UserRouting(Vertx vertx) {
		super(vertx);
	}

	public String getVerticleName() {
		return UserVerticle.class.getName();
	}

//	@Override
//	public String getCacheName() {
//		return "user";
//	}
}