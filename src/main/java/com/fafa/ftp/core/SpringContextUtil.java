package com.fafa.ftp.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Author Stark
 * @Date 2017年3月13日 下午5:02:21
 * @File SpringContextUtil.java
 */
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		context = ctx;

	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public static String getStringBean(String beanName) {
		return context.getBean(beanName, String.class);
	}

}
