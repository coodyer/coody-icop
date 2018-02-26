package org.coody.framework.box.init;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coody.framework.box.annotation.Around;
import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.annotation.OutBean;
import org.coody.framework.box.annotation.PathBinding;
import org.coody.framework.box.constant.BoxConstant;
import org.coody.framework.box.container.BeanContainer;
import org.coody.framework.box.container.MappingContainer;
import org.coody.framework.box.iface.InitFace;
import org.coody.framework.box.proyx.CglibProxy;
import org.coody.framework.util.ClassUtil;
import org.coody.framework.util.PrintException;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

public class BoxRute {
	
	private static final Logger logger = Logger.getLogger(BoxRute.class);

	static CglibProxy proxy = new CglibProxy();

	public static void init(String ...packets) throws Exception {
		Set<Class<?>> clazzs =new HashSet<Class<?>>();
		
		for(String packet:packets){
			Set<Class<?>> clazzsTemp=ClassUtil.getClasses(packet);
			clazzs.addAll(clazzsTemp);
		}
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		initAspect(clazzs);
		initClass(clazzs);
		initField();
		initMvc();
		initRun(clazzs);
	}

	public static void initAspect(Set<Class<?>> clas) {
		if (StringUtil.isNullOrEmpty(clas)) {
			return;
		}
		for (Class<?> cla : clas) {
			InitBean initBean = cla.getAnnotation(InitBean.class);
			if (StringUtil.isNullOrEmpty(initBean)) {
				continue;
			}
			Method[] methods = cla.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				Around around = method.getAnnotation(Around.class);
				if (around == null) {
					continue;
				}
				if (StringUtil.isNullOrEmpty(around.value())) {
					continue;
				}
				for (Class<?> clazz : around.value()) {
					BoxConstant.aspectMap.put(clazz, method);
				}
			}
		}
	}

	public static void initClass(Set<Class<?>> clas) throws Exception {
		if (StringUtil.isNullOrEmpty(clas)) {
			return;
		}
		for (Class<?> cla : clas) {
			String beanName = BeanContainer.getBeanName(cla);
			if (StringUtil.isNullOrEmpty(beanName)) {
				continue;
			}
			if (BeanContainer.containsBean(beanName)) {
				logger.error("存在重复的bean:" + beanName);
				continue;
			}
			BeanContainer.writeBean(beanName, proxy.getProxy(cla));
		}
	}

	public static void initRun(Set<Class<?>> clas) {
		if (StringUtil.isNullOrEmpty(clas)) {
			return;
		}
		for (Class<?> cla : clas) {
			if (!InitFace.class.isAssignableFrom(cla)) {
				continue;
			}
			try {
				InitFace face = BeanContainer.getBean(cla);
				if (StringUtil.isNullOrEmpty(face)) {
					continue;
				}
				face.init();
			} catch (Exception e) {
				PrintException.printException(logger, e);
			}
		}
	}

	public static void initField() throws Exception {
		for (Object bean : BeanContainer.getBeans()) {
			List<Field> fields = loadFields(bean.getClass());
			if (StringUtil.isNullOrEmpty(fields)) {
				continue;
			}
			for (Field field : fields) {
					OutBean writeBean = field.getAnnotation(OutBean.class);
					if (StringUtil.isNullOrEmpty(writeBean)) {
						continue;
					}
					String beanName = writeBean.beanName();
					if (StringUtil.isNullOrEmpty(beanName)) {
						beanName = field.getType().getName();
					}
					Object writeValue = null;
					field.setAccessible(true);
					if (!BeanContainer.containsBean(beanName)) {
						writeValue = proxy.getProxy(field.getClass());
						BeanContainer.writeBean(beanName, writeValue);
					}
					writeValue = BeanContainer.getBean(beanName);
					field.set(bean, writeValue);
			}
		}
	}

	public static void initMvc() throws Exception {
		for (Object bean : BeanContainer.getBeans()) {
			if (StringUtil.isNullOrEmpty(bean)) {
				continue;
			}
			PathBinding classBinding = bean.getClass().getAnnotation(PathBinding.class);
			if (StringUtil.isNullOrEmpty(classBinding)) {
				continue;
			}
			Method[] methods = bean.getClass().getDeclaredMethods();
			for (Method method : methods) {
				PathBinding methodBinding = method.getAnnotation(PathBinding.class);
				if (StringUtil.isNullOrEmpty(methodBinding)) {
					continue;
				}
				String path = formatPath(classBinding.value() + "/" + methodBinding.value());
				if(MappingContainer.containsPath(path)){
					logger.error("该地址已注册:"+path);
					continue;
				}
				MappingContainer.MvcMapping mapping=new MappingContainer.MvcMapping();
				mapping.setBean(bean);
				mapping.setPath(path);
				mapping.setMethod(method);
				mapping.setParamTypes(PropertUtil.getMethodParas(method));
				MappingContainer.writeMapping(mapping);
			}
		}
	}

	private static String formatPath(String path) {
		if (StringUtil.isNullOrEmpty(path)) {
			return null;
		}
		path = path.replace("\\", "/");
		while (path.contains("//")) {
			path = path.replace("//", "/");
		}
		if(!path.startsWith("/")){
			path="/"+path;
		}
		return path;
	}

	public static List<Field> loadFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		Field[] fieldArgs = clazz.getDeclaredFields();
		for (Field f : fieldArgs) {
			fields.add(f);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return fields;
		}
		List<Field> childFields = loadFields(superClass);
		if (StringUtil.isNullOrEmpty(childFields)) {
			return fields;
		}
		fields.addAll(childFields);
		return fields;
	}
}
