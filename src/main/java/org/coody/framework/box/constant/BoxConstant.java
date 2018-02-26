package org.coody.framework.box.constant;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.annotation.PathBinding;

public class BoxConstant {

	/**
	 * 初始化Bean拦截的注解
	 */
	public static final Class<?>[] beanAnnotations = new Class[] { InitBean.class, PathBinding.class };
	
	/**
	 * 切面存储。key注解类 value拦截器方法
	 */
	public static final Map<Class<?>, Method> aspectMap = new ConcurrentHashMap<Class<?>, Method>();
	
	/**
	 * 表主键列表
	 */
	public static final String table_primary_keys="table_primary_keys";
}
