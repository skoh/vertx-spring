package com.nemustech.indoornow.gateway;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.nemustech.indoornow.group.GroupRouting;
import com.nemustech.indoornow.user.UserRouting;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Gateway Verticle
 * 
 * @author skoh
 */
public class GatewayVerticle extends AbstractVerticle {
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * 어픞리케이션 컨텍스트
	 */
	protected ApplicationContext context;

	@Override
	public void start() throws Exception {
		super.start();

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		// 부서
		GroupRouting groupRouting = new GroupRouting(vertx);
		router.get("/group").handler(groupRouting::get);
		router.get("/group/:id").handler(groupRouting::get);
		router.post("/group").handler(groupRouting::post);
		router.put("/group").handler(groupRouting::put);
		router.delete("/group").handler(groupRouting::delete);
		router.delete("/group/:id").handler(groupRouting::delete);

		// 사용자
		UserRouting userRouting = new UserRouting(vertx);
		router.get("/user").handler(userRouting::get);
		router.get("/user/:id").handler(userRouting::get);
		router.post("/user").handler(userRouting::post);
		router.put("/user").handler(userRouting::put);
		router.delete("/user").handler(userRouting::delete);
		router.delete("/user/:id").handler(userRouting::delete);

		// 부서 사용자
		GroupUserRouting groupUserRouting = new GroupUserRouting(vertx);
		router.post("/group_user").handler(groupUserRouting::post);

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

	public GatewayVerticle(ApplicationContext context) {
		this.context = context;
	}
}
