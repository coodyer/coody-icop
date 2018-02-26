package org.coody.framework.box.proyx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.box.constant.BoxConstant;
import org.coody.framework.box.container.BeanContainer;
import org.coody.framework.box.wrapper.AspectWrapper;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor {


	/**
	 * key拦截方法，value拦截器的方法
	 */
	public static final Map<Method, Set<Method>> interceptMap = new ConcurrentHashMap<Method, Set<Method>>();

	public Object getProxy(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		Method[] methods = clazz.getDeclaredMethods();
		if (StringUtil.isNullOrEmpty(methods)) {
			return clazz.newInstance();
		}
		boolean needAspect = false;
		for (Method method : methods) {
			Annotation[] arounds = method.getAnnotations();
			for (Annotation around : arounds) {
				if (!BoxConstant.aspectMap.containsKey(around.annotationType())) {
					continue;
				}
				needAspect = true;
				Method aspectMethod = BoxConstant.aspectMap.get(around.annotationType());
				if (interceptMap.containsKey(method)) {
					interceptMap.get(method).add(aspectMethod);
					continue;
				}
				Set<Method> aspectMethods = new HashSet<Method>();
				aspectMethods.add(aspectMethod);
				interceptMap.put(method, aspectMethods);
			}
		}
		if (!needAspect) {
			return clazz.newInstance();
		}
		Enhancer enhancer = new Enhancer();
		// 设置需要创建子类的类
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		return enhancer.create();
	}

	// 拦截父类所有方法的调用
	public Object intercept(Object bean, Method method, Object[] params, MethodProxy proxy) throws Throwable {
		if (!interceptMap.containsKey(method)) {
			return proxy.invokeSuper(bean, params);
		}
		List<Method> invokeMethods = new ArrayList<Method>(interceptMap.get(method));
		Method invokeMethod = invokeMethods.get(0);
		invokeMethods.remove(0);
		Class<?> clazz = PropertUtil.getClass(invokeMethod);
		Object invokeBean = BeanContainer.getBean(clazz);
		AspectWrapper wrapper = new AspectWrapper();
		wrapper.setBean(bean);
		wrapper.setMethod(method);
		wrapper.setParams(params);
		wrapper.setProxy(proxy);
		wrapper.setClazz(bean.getClass());
		AspectWrapper childWrapper = parseAspect(bean,wrapper, invokeMethods);
		if (childWrapper != null) {
			wrapper.setBean(invokeBean);
			wrapper.setChildAspect(childWrapper);
		}
		return invokeMethod.invoke(invokeBean, wrapper);
	}

	private AspectWrapper parseAspect(Object lastBean,AspectWrapper baseWrapper, List<Method> invokeMethods) {
		if (StringUtil.isNullOrEmpty(invokeMethods)) {
			return null;
		}
		Method currentAspectMethod = invokeMethods.get(0);
		invokeMethods.remove(0);
		AspectWrapper wrapper = new AspectWrapper();
		wrapper.setBean(baseWrapper.getBean());
		wrapper.setMethod(baseWrapper.getMethod());
		wrapper.setParams(baseWrapper.getParams());
		wrapper.setProxy(baseWrapper.getProxy());
		wrapper.setClazz(baseWrapper.getClazz());
		wrapper.setCurrentAspectMethod(currentAspectMethod);
		AspectWrapper childWrapper = parseAspect(lastBean,baseWrapper, invokeMethods);
		if (childWrapper != null) {
			wrapper.setChildAspect(childWrapper);
			return wrapper;
		}
		wrapper.setBean(lastBean);
		return wrapper;
	}
}
