package org.coody.framework.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.coody.framework.entity.BeanEntity;
import org.coody.web.comm.constant.CacheFinal;

public class AspectUtil {

	public static ThreadLocal<Map<String, Object>> moduleThread = new ThreadLocal<Map<String, Object>>();

	public static String getFieldKey(Class<?> clazz, Method method, Object[] paras, String key, String[] fields) {
		if (StringUtil.isNullOrEmpty(key)) {
			key = getMethodKey(clazz, method);
			key = key.replace(".", "_");
			key = key.replace(",", "_");
		}
		StringBuilder paraKey = new StringBuilder();
		for (String field : fields) {
			Object paraValue = AspectUtil.getMethodPara(method, field, paras);
			if (StringUtil.isNullOrEmpty(paraValue)) {
				paraValue = "";
			}
			paraKey.append("_").append(JSONWriter.write(paraValue));
		}
		key = key + "_" + EncryptUtil.md5Code(paraKey.toString());
		return key;
	}

	// 将对象内所有字段名、字段值拼接成字符串，用于缓存Key
	public static String getBeanKey(Object... obj) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return "";
		}
		String str = JSONWriter.write(obj);
		return EncryptUtil.md5Code(str);
	}

	public static String getMethodKey(Class<?> clazz, Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName()).append(".").append(method.getName());
		Class<?>[] paraTypes = method.getParameterTypes();
		sb.append("(");
		if (!StringUtil.isNullOrEmpty(paraTypes)) {
			for (int i = 0; i < paraTypes.length; i++) {
				sb.append(paraTypes[i].getName());
				if (i < paraTypes.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return CacheFinal.AUTO_CACHE_KEY + "-" + sb.toString();
	}

	public static Object getMethodPara(Method method, String fieldName, Object[] args) {
		List<BeanEntity> beanEntitys = PropertUtil.getMethodParas(method);
		if (StringUtil.isNullOrEmpty(beanEntitys)) {
			return "";
		}
		String[] fields = fieldName.split("\\.");
		BeanEntity entity = (BeanEntity) PropertUtil.getByList(beanEntitys, "fieldName", fields[0]);
		if (StringUtil.isNullOrEmpty(entity)) {
			return "";
		}
		Object para = args[beanEntitys.indexOf(entity)];
		if (fields.length > 1 && para != null) {
			for (int i = 1; i < fields.length; i++) {
				para = PropertUtil.getFieldValue(para, fields[i]);
			}
		}
		return para;
	}


}
