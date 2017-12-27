package com.nemustech.indoornow.gateway;

import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nemustech.common.util.BeanUtil;
import com.nemustech.common.util.StringUtil;
import com.nemustech.indoornow.group.Group;
import com.nemustech.indoornow.group.GroupVerticle;
import com.nemustech.indoornow.user.User;
import com.nemustech.indoornow.user.UserVerticle;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.RoutingContext;

/**
 * 부서 사용자 Routing
 */
public class GroupUserRouting {
	/**
	 * 로그
	 */
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * 캐시키 형식
	 */
	protected MessageFormat cacheKeyFormat = new MessageFormat(getCacheName() + "_" + getClass().getName() + "_{0}");

	/**
	 * 캐시맵
	 */
	protected LocalMap<String, String> cacheMap;

	/**
	 * Vertx
	 */
	protected Vertx vertx;

	public GroupUserRouting(Vertx vertx) {
		// 캐시 설정
		if (isCacheable())
			cacheMap = vertx.sharedData().getLocalMap("routing_" + getCacheName());

		this.vertx = vertx;
	}

	/**
	 * 캐시명
	 * 
	 * @return null 은 캐시 사용 안함
	 */
	public String getCacheName() {
		return "group_user";
	}

	/**
	 * 캐시 사용여부
	 * 
	 * @return
	 */
	public boolean isCacheable() {
		return getCacheName() != null;
	}

	/**
	 * 조회
	 * 
	 * @param routingContext
	 */
	public void post(RoutingContext routingContext) {
		HttpServerRequest req = routingContext.request();
		HttpServerResponse res = routingContext.response();
		res.setChunked(true);

		// 파라미터 변환
		JsonObject json = routingContext.getBodyAsJson();
		log.info("params: " + json);
		Group group = BeanUtil.convertMapToObject(((JsonObject) json.getValue("group")).getMap(), Group.class);
		User user = BeanUtil.convertMapToObject(((JsonObject) json.getValue("user")).getMap(), User.class);

		// 서비스 실행
		String address = "get";
		if (isCacheable()) {
			// 캐시 조회
			log.trace("cacheKeys(" + getCacheName() + "): " + cacheMap.keySet());
			String cacheKey = cacheKeyFormat.format(new Object[] {
					StringUtil.toString(group, "conditionObj") + "," + StringUtil.toString(user, "conditionObj") });
			log.debug("cacheKey: " + cacheKey);
			String cache = cacheMap.get(cacheKey);

			if (cache == null) {
				send(group, user, res, address, cacheKey);
			} else {
				res.setStatusCode(200).write(cache).end();
			}
		} else {
			send(group, user, res, address, null);
		}
	}

	protected void send(Group group, User user, HttpServerResponse res, String address, String cacheKey) {
		vertx.eventBus().<String>send(GroupVerticle.class.getName() + "_" + address, Json.encode(group), result -> {
			if (result.succeeded()) {
				send(user, res, address, cacheKey, result);
			} else {
				res.setStatusCode(500).write(result.cause().toString()).end();
			}
		});
	}

	protected void send(User user, HttpServerResponse res, String address, String cacheKey,
			AsyncResult<Message<String>> resultGroup) {
		vertx.eventBus().<String>send(UserVerticle.class.getName() + "_" + address, Json.encode(user), result -> {
			if (result.succeeded()) {
				// Merge
				Group[] groups = Json.decodeValue(resultGroup.result().body(), Group[].class);
				User[] users = Json.decodeValue(result.result().body(), User[].class);
				Arrays.stream(groups).forEach(group -> {
					group.setUsers(Arrays.stream(users).filter(x -> group.getId().equals(x.getGroup_id())).toArray());
				});

				// 캐시 저장
				if (isCacheable())
					cacheMap.put(cacheKey, Json.encode(groups));

				res.setStatusCode(200).write(Json.encode(groups)).end();
			} else {
				res.setStatusCode(500).write(result.cause().toString()).end();
			}
		});
	}
}
