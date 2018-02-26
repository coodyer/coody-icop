package org.coody.framework.box.container;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.box.constant.BoxConstant;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

@SuppressWarnings({"unchecked","rawtypes"})
public class BeanContainer {
	
	private static Map<String, Object> beanMap=new ConcurrentHashMap<String, Object>();
	
	public static <T> T getBean(Class<?> cla){
		String beanName=getBeanName(cla);
		return (T) beanMap.get(beanName);
	}
	
	public static <T> T getBean(String beanName){
		return (T) beanMap.get(beanName);
	}
	public static void writeBean(String beanName,Object bean){
		beanMap.put(beanName, bean);
	}
	public static boolean containsBean(String beanName){
		return beanMap.containsKey(beanName);
	}
	public static Collection<?> getBeans(){
		return beanMap.values();
	}
	public static String getBeanName(Class<?> clazz){
		for (Class annotationClass : BoxConstant.beanAnnotations) {
			Annotation initBean = clazz.getAnnotation(annotationClass);
			if (StringUtil.isNullOrEmpty(initBean)) {
				continue;
			}
			String beanName = (String) PropertUtil.getFieldValue(initBean, "value");
			if (StringUtil.isNullOrEmpty(beanName)) {
				beanName = clazz.getName();
			}
			return beanName;
		}
		return null;
	}
}
