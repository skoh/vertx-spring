package com.nemustech.indoornow.common;

import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.nemustech.common.function.CheckedSupplier;
import com.nemustech.common.model.Default;
import com.nemustech.common.service.CommonService;
import com.nemustech.common.util.StringUtil;
import com.nemustech.common.util.Utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.LocalMap;

/**
 * 기본 CRUD 추상 Verticle
 * 
 * @param <T>
 * @author skoh
 */
public abstract class AbstractCRUDVerticle<T extends Default> extends AbstractVerticle {
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
	 * 어픞리케이션 컨텍스트
	 */
	protected ApplicationContext context;

	/**
	 * 서비스
	 */
	protected CommonService<T> service;

	/**
	 * MultiMap<Map.Entry<String, String>>을 Map<String, Object>으로 변환한다.
	 * 
	 * @param multiMap
	 * @return
	 */
	public static Map<String, Object> convertMultiMapToMap(MultiMap multiMap) {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<Map.Entry<String, String>> iterator = multiMap.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			map.put(entry.getKey(), entry.getValue());
		}

		return map;
	}

	public AbstractCRUDVerticle(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public void start() throws Exception {
		// 캐시 설정
		if (isCacheable())
			cacheMap = vertx.sharedData().getLocalMap("verticle_" + getCacheName());

		// 이벤트 수신
		EventBus eventBus = vertx.eventBus();
		eventBus.<String>consumer(getClass().getName() + "_" + "get").handler(get());
		eventBus.<String>consumer(getClass().getName() + "_" + "post").handler(post());
		eventBus.<String>consumer(getClass().getName() + "_" + "put").handler(put());
		eventBus.<String>consumer(getClass().getName() + "_" + "delete").handler(delete());
	}

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
	 * @return
	 */
	public Handler<Message<String>> get() {
		return msg -> {
			// 모델 변환
			T t = Json.decodeValue(msg.body(), modelType);

			// 서비스 실행
			if (isCacheable()) {
				// 캐시 조회
				log.trace("cacheKeys(" + getCacheName() + "): " + cacheMap.keySet());
				String cacheKey = cacheKeyFormat.format(new Object[] { StringUtil.toString(t, "conditionObj") });
				log.debug("cacheKey: " + cacheKey);
				String cache = cacheMap.get(cacheKey);

				if (cache == null) {
					select(t, msg, cacheKey);
				} else {
					msg.reply(cache);
				}
			} else {
				select(t, msg, null);
			}
		};
	}

	/**
	 * 생성
	 * 
	 * @return
	 */
	public Handler<Message<String>> post() {
		return msg -> {
			// 모델 변환
			T t = Json.decodeValue(msg.body(), modelType);

			// 서비스 실행
			update(t, msg, () -> {
				return service.insert(t).toString();
			});
		};
	}

	/**
	 * 수정
	 * 
	 * @return
	 */
	public Handler<Message<String>> put() {
		return msg -> {
			// 모델 변환
			T t = Json.decodeValue(msg.body(), modelType);
			t.setCondition("name = 'name'");

			// 서비스 실행
			update(t, msg, () -> {
				return String.valueOf(service.update(t));
			});
		};
	}

	/**
	 * 삭제
	 * 
	 * @return
	 */
	public Handler<Message<String>> delete() {
		return msg -> {
			// 모델 변환
			T t = Json.decodeValue(msg.body(), modelType);

			// 서비스 실행
			update(t, msg, () -> {
				return String.valueOf(service.delete(t));
			});
		};
	}

	/**
	 * 조회
	 * 
	 * @param t 모델
	 * @param msg 메세지
	 * @param cacheKey 캐시키
	 */
	protected void select(T t, Message<String> msg, String cacheKey) {
		async(t, () -> {
			List<T> list = service.list(t);
			String result = Json.prettyMapper.writeValueAsString(list);

			// 캐시 저장
			if (isCacheable())
				cacheMap.put(cacheKey, result);

			return result;
		}, result -> {
			msg.reply(result.result());
		}, result -> {
			msg.reply(Utils.toString(result.cause()));
		});
	}

	/**
	 * 갱신
	 * 
	 * @param t 모델
	 * @param msg 메세지
	 * @param service 서비스 함수
	 */
	protected void update(T t, Message<String> msg, CheckedSupplier<String> service) {
		async(t, service, result -> {
			// 캐시 삭제
			if (isCacheable())
				cacheMap.clear();

			msg.reply(result.result());
		}, result -> {
			msg.reply(Utils.toString(result.cause()));
		});
	}

	/**
	 * 비동기(워커 쓰레드)
	 * 
	 * @param t 모델
	 * @param service 서비스 함수
	 * @param success 성공 함수
	 * @param fail 실패 함수
	 */
	protected void async(T t, CheckedSupplier<String> service, Consumer<AsyncResult<String>> success,
			Consumer<AsyncResult<String>> fail) {
		vertx.<String>executeBlocking(future -> {
			try {
				String result = service.get();

				future.complete(result);
			} catch (Exception e) {
				log.error(t.toString(), e);

				future.fail(e);
			}
		}, result -> {
			if (result.succeeded()) {
				success.accept(result);
			} else {
				fail.accept(result);
			}
		});
	}
}
