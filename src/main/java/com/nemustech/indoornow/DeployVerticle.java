package com.nemustech.indoornow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nemustech.indoornow.common.Config;
import com.nemustech.indoornow.gateway.GatewayVerticle;
import com.nemustech.indoornow.group.GroupVerticle;
import com.nemustech.indoornow.user.UserVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

/**
 * 배포 Verticle
 * 
 * <pre>
 * 1. 개발환경
 * - Vertx v3.3.3
 * - Spring Boot v1.4.2
 * - iBatis v3.1.1
 * 
 * - link : <a href="https://nemus-hi.atlassian.net/browse/INV3SER-23">nemus-hi.atlassian.net</a>
 * - lombok v1.16.12
 * - logback v1.1.7
 * - log4jdbc-remix v0.2.7
 * 
 * 2. 구조
 * Client <-http(s)-> GatewayVerticle <-eventBus-> GroupVerticle <-jdbc-> DB(MySQL,Oracle,MS-SQL)
 *                                    <-eventBus-> UserVerticle  <-jdbc-> DB
 * </pre>
 */
public class DeployVerticle extends AbstractVerticle {
	protected Log log = LogFactory.getLog(getClass());

	@Override
	public void start() throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		if (vertx == null)
			vertx = Vertx.vertx();

		// Exception in thread "main" java.lang.IllegalArgumentException: Can't specify > 1 instances for already created verticle
//		DeploymentOptions options = new DeploymentOptions().setWorker(true);//.setInstances(2);
		vertx.deployVerticle(new GroupVerticle(context));// , options);
		vertx.deployVerticle(new UserVerticle(context));
		vertx.deployVerticle(new GatewayVerticle(context));

		log.info("Deployment done");
	}

	public static void main(String[] args) throws Exception {
		new DeployVerticle().start();
	}
}
