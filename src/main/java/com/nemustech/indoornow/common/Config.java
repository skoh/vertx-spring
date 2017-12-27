package com.nemustech.indoornow.common;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.nemustech.web.util.WebApplicationContextUtil;

/**
 * 설정
 * 
 * @author skoh
 */
@Configuration
@ImportResource({ "classpath:config-spring.xml" })
public class Config {
	@Autowired
	protected ApplicationContext context;

	@PostConstruct
	protected void init() {
//		WebApplicationContextUtil.printBeans(context, true);
	}
}
