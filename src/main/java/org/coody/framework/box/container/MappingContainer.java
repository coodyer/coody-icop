package org.coody.framework.box.container;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.entity.BaseModel;
import org.coody.framework.entity.BeanEntity;

@SuppressWarnings("unchecked")
public class MappingContainer {

	private static Map<String, Object> mvcMap = new ConcurrentHashMap<String, Object>();

	public static <T> T getMapping(String path) {
		return (T) mvcMap.get(path);
	}

	public static void writeMapping(MvcMapping mvcMapping) {
		mvcMap.put(mvcMapping.getPath(), mvcMapping);
	}

	public static boolean containsPath(String path) {
		return mvcMap.containsKey(path);
	}

	public static Collection<?> getBeans() {
		return mvcMap.values();
	}

	@SuppressWarnings("serial")
	public static class MvcMapping extends BaseModel {

		private String path;

		private Method method;

		private Object bean;

		private List<BeanEntity> paramTypes;
		

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Object getBean() {
			return bean;
		}

		public void setBean(Object bean) {
			this.bean = bean;
		}

		public List<BeanEntity> getParamTypes() {
			return paramTypes;
		}

		public void setParamTypes(List<BeanEntity> paramTypes) {
			this.paramTypes = paramTypes;
		}

	}
}
