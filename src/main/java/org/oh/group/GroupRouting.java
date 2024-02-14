package org.oh.group;

import org.oh.common.AbstractCRUDRouting;

import io.vertx.core.Vertx;

/**
 * 부서 Routing
 */
public class GroupRouting extends AbstractCRUDRouting<Group> {
	public GroupRouting(Vertx vertx) {
		super(vertx);
	}

	public String getVerticleName() {
		return GroupVerticle.class.getName();
	}

//	@Override
//	public String getCacheName() {
//		return "group";
//	}
}
