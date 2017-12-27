package com.nemustech.indoornow.common;

import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nemustech.common.model.Default;
import com.nemustech.common.util.BeanUtil;
import com.nemustech.common.util.StringUtil;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.RoutingContext;

/**
 * 기본 CRUD 추상 Routing
 * 
 * @author skoh
 */
public abstract class AbstractCRUDRouting<T extends Default> {
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
	 * 모델 타입
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> modelType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0];

	/**
	 * Vertx
	 */
	protected Vertx vertx;

	public AbstractCRUDRouting(Vertx vertx) {
		// 캐시 설정
		if (isCacheable())
			cacheMap = vertx.sharedData().getLocalMap("routing_" + getCacheName());

		this.vertx = vertx;
	}

	/**
	 * Verticle명
	 * 
	 * @return
	 */
	public abstract String getVerticleName();

	/**
	 * 캐시명
	 * 
	 * @return null 은 캐시 사용 안함
	 */
	public String getCacheName() {
		return null;
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
	public void get(RoutingContext routingContext) {
		HttpServerRequest req = routingContext.request();
		HttpServerResponse res = routingContext.response();
		res.setChunked(true);

		// 파라미터 변환
		MultiMap params = req.params();
		log.info("params: " + params);
		T t = BeanUtil.convertMapToObject(AbstractCRUDVerticle.convertMultiMapToMap(params), modelType);

		// 서비스 실행
		select(t, res, "get");
	}

	/**
	 * 생성
	 * 
	 * @param routingContext
	 */
	public void post(RoutingContext routingContext) {
		HttpServerResponse res = routingContext.response();
		res.setChunked(true);

		// 파라미터 변환
		JsonObject json = routingContext.getBodyAsJson();
		log.info("params: " + json);
		T t = BeanUtil.convertMapToObject(json.getMap(), modelType);

		// 서비스 실행
		update(t, res, "post");
	}

	/**
	 * 수정
	 * 
	 * @param routingContext
	 */
	public void put(RoutingContext routingContext) {
		HttpServerResponse res = routingContext.response();
		res.setChunked(true);

		// 파라미터 변환
		JsonObject json = routingContext.getBodyAsJson();
		log.info("params: " + json);
		T t = BeanUtil.convertMapToObject(json.getMap(), modelType);

		// 서비스 실행
		update(t, res, "put");
	}

	/**
	 * 삭제
	 * 
	 * @param routingContext
	 */
	public void delete(RoutingContext routingContext) {
		HttpServerRequest req = routingContext.request();
		HttpServerResponse res = routingContext.response();
		res.setChunked(true);

		// 파라미터 변환
		MultiMap params = req.params();
		log.info("params: " + params);
		T t = BeanUtil.convertMapToObject(AbstractCRUDVerticle.convertMultiMapToMap(params), modelType);

		// 서비스 실행
		update(t, res, "delete");
	}

	/**
	 * 조회
	 * 
	 * @param t 모델
	 * @param res 응답
	 * @param address 주소
	 */
	protected void select(T t, HttpServerResponse res, String address) {
		if (isCacheable()) {
			// 캐시 조회
			log.trace("cacheKeys(" + getCacheName() + "): " + cacheMap.keySet());
			String cacheKey = cacheKeyFormat.format(new Object[] { StringUtil.toString(t, "conditionObj") });
			log.debug("cacheKey: " + cacheKey);
			String cache = cacheMap.get(cacheKey);

			if (cache == null) {
				send(t, res, address, cacheKey);
			} else {
				res.setStatusCode(200).write(cache).end();
			}
		} else {
			send(t, res, address, null);
		}
	}

	/**
	 * 전송
	 * 
	 * @param t 모델
	 * @param res 응답
	 * @param address 주소
	 * @param cacheKey 캐시키
	 */
	protected void send(T t, HttpServerResponse res, String address, String cacheKey) {
		vertx.eventBus().<String>send(getVerticleName() + "_" + address, Json.encode(t), result -> {
			if (result.succeeded()) {
				// 캐시 저장
				if (isCacheable())
					cacheMap.put(cacheKey, result.result().body());

				res.setStatusCode(200).write(result.result().body()).end();
			} else {
				res.setStatusCode(500).write(result.cause().toString()).end();
			}
		});
	}

	/**
	 * 갱신
	 * 
	 * @param t 모델
	 * @param res 응답
	 * @param address 주소
	 */
	protected void update(T t, HttpServerResponse res, String address) {
		vertx.eventBus().<String>send(getVerticleName() + "_" + address, Json.encode(t), result -> {
			if (result.succeeded()) {
				// 캐시 삭제
				if (isCacheable())
					cacheMap.clear();

				res.setStatusCode(200).write(result.result().body()).end();
			} else {
				res.setStatusCode(500).write(result.cause().toString()).end();
			}
		});
	}
}