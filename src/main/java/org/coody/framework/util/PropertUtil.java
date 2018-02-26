package org.coody.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.entity.BeanEntity;
import org.coody.framework.entity.Record;

@SuppressWarnings("unchecked")
public class PropertUtil {

	private static Map<Class<?>, List<Field>> fieldMap = new ConcurrentHashMap<Class<?>, List<Field>>();
	private static Map<Class<?>, List<Method>> methodMap = new ConcurrentHashMap<Class<?>, List<Method>>();
	private static Map<Method, List<BeanEntity>> paramMap = new ConcurrentHashMap<Method, List<BeanEntity>>();

	public static void reload() {
		fieldMap.clear();
		methodMap.clear();
	}

	/**
	 * 获取对象多个字段的值
	 * 
	 * @param obj
	 * @param fieldNames
	 * @return
	 */
	public static List<Object> getFieldValues(Object obj, String... fieldNames) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return null;
		}
		List<Object> values = new ArrayList<Object>(fieldNames.length * 2);
		for (String fieldName : fieldNames) {
			values.add(getFieldValue(obj, fieldName));
		}
		if (StringUtil.isNullOrEmpty(values)) {
			return null;
		}
		return values;
	}

	/**
	 * 从对象中获取目标方法
	 * 
	 * @param methods
	 *            方法数组
	 * @param methodName
	 *            方法名称
	 * @param paras
	 *            参数列表
	 * @return
	 */
	public static Method getTargeMethod(Method[] methods, String methodName, Object... paras) {
		for (Method m : methods) {
			if (isTargeMethod(m, methodName, paras)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * 判断目标是否是当前方法
	 * 
	 * @param method
	 *            当前方法
	 * @param methodName
	 *            目标方法名
	 * @param paras
	 *            目标方法参数列表
	 * @return
	 */
	private static boolean isTargeMethod(Method method, String methodName, Object... paras) {
		if (!method.getName().equals(methodName)) {
			return false;
		}
		Class<?>[] clas = method.getParameterTypes();
		if (StringUtil.isNullOrEmpty(clas) && StringUtil.isNullOrEmpty(paras)) {
			return true;
		}
		if (StringUtil.isNullOrEmpty(clas) || StringUtil.isNullOrEmpty(paras)) {
			return false;
		}
		if (clas.length != paras.length) {
			return false;
		}
		for (int i = 0; i < clas.length; i++) {
			if (paras[i] == null) {
				continue;
			}
			if (!clas[i].isAssignableFrom(paras[i].getClass())) {
				return false;
			}
		}
		return true;
	}

	public static <T> T copyPropertys(Object source, Object targe) {
		if (StringUtil.isNullOrEmpty(source)) {
			return (T) targe;
		}
		List<BeanEntity> entitys = getBeanFields(source);
		if (StringUtil.isNullOrEmpty(entitys)) {
			return (T) targe;
		}
		for (BeanEntity entity : entitys) {
			try {
				setFieldValue(targe, entity.getFieldName(), entity.getFieldValue());
			} catch (Exception e) {
			}
		}
		return (T) targe;
	}

	/**
	 * 对象相同字段组成新list
	 * 
	 * @param list
	 * @param cla
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> List<T> getNewList(List list, Class cla) {
		if (StringUtil.findNull(list, cla) > -1) {
			return null;
		}
		List<T> ls = new ArrayList<T>();
		for (Object obj : list) {
			try {
				Object newObj = cla.newInstance();
				newObj = copyPropertys(obj, newObj);
				ls.add((T) newObj);
			} catch (Exception e) {
			}
		}
		return ls;
	}

	/**
	 * Map转对象
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> T mapToModel(Map map, Class<?> clazz) {
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		try {
			T value = (T) clazz.newInstance();
			List<BeanEntity> entitys = getBeanFields(clazz);
			if (StringUtil.isNullOrEmpty(entitys)) {
				return null;
			}
			for (BeanEntity entity : entitys) {
				try {
					entity.getSourceField().setAccessible(true);
					entity.getSourceField().set(value,
							parseValue(map.get(entity.getFieldName()), entity.getFieldType()));
				} catch (Exception e) {
				}
			}
			return value;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取某个对象的class
	 * 
	 * @param obj
	 * @return
	 */
	public static Class<? extends Object> getObjClass(Object obj) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return null;
		}
		if (obj instanceof Class) {
			return (Class<?>) obj;
		}
		return obj.getClass();
	}

	/**
	 * 获取class的字段对象
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		List<Field> fields = loadFields(clazz);
		if (StringUtil.isNullOrEmpty(fields)) {
			return null;
		}
		for (Field f : fields) {
			if (f.getName().equals(fieldName)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * 一个神奇的方法：获取对象字段集合
	 * 
	 * @param obj
	 * @return
	 */
	public static List<BeanEntity> getBeanFields(Object obj) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return null;
		}
		Class<? extends Object> cla = getObjClass(obj);
		List<BeanEntity> infos = getClassFields(cla);
		if (StringUtil.isNullOrEmpty(infos)) {
			return infos;
		}
		if (obj instanceof java.lang.Class) {
			return infos;
		}
		for (BeanEntity info : infos) {
			try {
				Field f = info.getSourceField();
				f.setAccessible(true);
				Object value = f.get(obj);
				info.setFieldValue(value);
			} catch (Exception e) {

			}
		}
		return infos;
	}

	/**
	 * 一个神奇的方法：获取class字段集合
	 * 
	 * @param cla
	 * @return
	 */
	public static List<BeanEntity> getClassFields(Class<?> cla) {
		try {
			List<Field> fields = loadFields(cla);
			List<BeanEntity> infos = new ArrayList<BeanEntity>();
			for (Field f : fields) {
				if (f.getName().equalsIgnoreCase("serialVersionUID")) {
					continue;
				}
				BeanEntity tmp = new BeanEntity();
				tmp.setSourceField(f);
				tmp.setFieldAnnotations(f.getAnnotations());
				tmp.setFieldName(f.getName());
				tmp.setFieldType(f.getType());
				infos.add(tmp);
			}
			return infos;
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * 一个神奇的方法：从一个List提取字段名统一的分组
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> List<T> getGroup(List<?> objs, String fieldName, Object fieldValue) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, List> map = PropertUtil.listToMaps(objs, fieldName);
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map.get(fieldValue);
	}

	/**
	 * 从一个集合获取某指定字段值第一个对象
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> T getByList(List<?> objs, String fieldName, Object fieldValue) {
		if (StringUtil.findNull(objs, fieldName, fieldValue) > -1) {
			return null;
		}
		Map map = PropertUtil.listToMap(objs, fieldName);
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return (T) map.get(fieldValue);
	}

	/**
	 * 获取对象某个字段值
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	private static Object getFieldValueCurr(Object obj, String fieldName) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return null;
		}
		Field f = getField(obj.getClass(), fieldName);
		if (StringUtil.isNullOrEmpty(f)) {
			return null;
		}
		f.setAccessible(true);
		try {
			return f.get(obj);
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * 获取字段值，支持点属性
	 * 
	 * @param bean
	 * @param paraName
	 * @return
	 */
	public static Object getFieldValue(Object bean, String paraName) {
		if (StringUtil.isNullOrEmpty(bean)) {
			return null;
		}
		List<BeanEntity> beanEntitys = PropertUtil.getBeanFields(bean);
		if (StringUtil.isNullOrEmpty(beanEntitys)) {
			return null;
		}
		if (!paraName.contains(".")) {
			return PropertUtil.getFieldValueCurr(bean, paraName);
		}
		List<String> fields = new ArrayList<String>(Arrays.asList(paraName.split("\\.")));
		Object beanTmp = PropertUtil.getFieldValue(bean, fields.get(0));
		fields.remove(0);
		return getFieldValue(beanTmp, StringUtil.collectionMosaic(fields, "."));
	}

	/**
	 * 获取方法的类
	 * 
	 * @param method
	 * @return
	 */
	public static Class<?> getClass(Method method) {
		Class<?> cla = (Class<?>) PropertUtil.getFieldValue(method, "clazz");
		return cla;
	}

	/**
	 * 获取List对象某个字段的值组成新List
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static <T> List<T> getFieldValues(List<?> objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		Object value;
		for (Object obj : objs) {
			value = getFieldValue(obj, fieldName);
			list.add(value);
		}
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		return (List<T>) list;
	}

	/**
	 * 获取对象字段列表
	 * 
	 * @param cla
	 * @return
	 */
	public static List<String> getFieldNames(Class<?> cla) {
		Field[] fields = cla.getDeclaredFields();
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj
	 *            实例对象
	 * @param propertyName
	 *            属性名
	 * @param value
	 *            新的字段值
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void setFieldValue(Object object, String propertyName, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		Field field = getField(object.getClass(), propertyName);
		if (StringUtil.isNullOrEmpty(field)) {

			return;
		}
		setFieldValue(object, field, value);
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj
	 *            实例对象
	 * @param propertyName
	 *            属性名
	 * @param value
	 *            新的字段值
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void setFieldValue(Object object, Field field, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		if (field.getType().isEnum()) {
			setFieldValue(field, "name", value);
			Object enmValue = field.get(object);
			setFieldValue(enmValue, "name", value);
			return;
		}
		if (Modifier.isFinal(field.getModifiers())) {
			int modifiers = field.getModifiers();
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				try {
					modifiersField.setAccessible(true);
					modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
					Object obj = parseValue(value, field.getType());
					field.set(object, obj);
				} catch (IllegalAccessException e) {

					if (!StringUtil.isNullOrEmpty(PropertUtil.getFieldValue(field, "fieldAccessor"))) {
						setProperties(field, "fieldAccessor.isReadOnly", false);
						setProperties(field, "fieldAccessor.isFinal", false);
						setProperties(field, "fieldAccessor.field", field);
					}
					if (!StringUtil.isNullOrEmpty(PropertUtil.getFieldValue(field, "overrideFieldAccessor"))) {
						setProperties(field, "overrideFieldAccessor.isReadOnly", false);
						setProperties(field, "overrideFieldAccessor.isFinal", false);
						setProperties(field, "overrideFieldAccessor.field", field);
					}

					setFieldValue(field, "root", field);
					setFieldValue(object, field, value);
				} catch (Exception e) {

				} finally {
					if (modifiers != field.getModifiers()) {
						modifiersField.set(field, modifiers);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return;
		}
		Object obj = parseValue(value, field.getType());
		field.set(object, obj);
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj
	 *            实例对象
	 * @param propertyName
	 *            属性名
	 * @param value
	 *            新的字段值
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public static void setProperties(Object object, String propertyName, Object value)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		if (StringUtil.isNullOrEmpty(object)) {
			return;
		}
		List<BeanEntity> beanEntitys = PropertUtil.getBeanFields(object);
		if (StringUtil.isNullOrEmpty(beanEntitys)) {
			return;
		}
		if (!propertyName.contains(".")) {
			setFieldValue(object, propertyName, value);
			return;
		}
		List<String> fields = new ArrayList<String>(Arrays.asList(propertyName.split("\\.")));
		String fieldName = fields.get(0);
		BeanEntity currField = PropertUtil.getByList(beanEntitys, "fieldName", fieldName);
		if (currField == null || (currField.getFieldValue() == null && value == null)) {
			return;
		}
		Object beanTmp = currField.getFieldValue();
		if (beanTmp == null) {
			beanTmp = currField.getFieldType().newInstance();
		}
		fields.remove(0);
		setProperties(beanTmp, StringUtil.collectionMosaic(fields, "."), value);
		setProperties(object, fieldName, beanTmp);
	}

	/**
	 * 设置集合对象某字段值
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldsValue
	 * @return
	 */
	public static List<?> setFieldValues(List<?> objs, String fieldName, Object fieldsValue) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		try {
			for (Object obj : objs) {
				try {
					if (StringUtil.isNullOrEmpty(obj)) {
						continue;
					}
					setProperties(obj, fieldName, fieldsValue);
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {

		}
		return objs;
	}

	/**
	 * 一个神奇的方法：一个List根据某个字段排序
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T> List<T> doSeq(List<?> objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, List> maps = listToMaps(objs, fieldName);
		if (StringUtil.isNullOrEmpty(maps)) {
			return null;
		}
		List list = new ArrayList();
		for (Object key : maps.keySet()) {
			try {
				list.addAll(maps.get(key));
			} catch (Exception e) {

			}
		}
		return list;
	}

	/**
	 * 一个神奇的方法：一个List根据某个字段排序
	 * 
	 * @param objs
	 * @param fieldName
	 * @param isDesc
	 * @return
	 */
	public static <T> List<T> doSeqDesc(List<?> objs, String fieldName) {
		List<T> list = doSeq(objs, fieldName);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		Collections.reverse(list);
		return list;
	}

	/**
	 * 一个List转为Map，fieldName作为Key，所有字段值相同的组成List作为value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map<Object, List> listToMaps(List objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, List> map = new TreeMap<Object, List>();
		List<Object> list;
		for (Object obj : objs) {
			try {
				Object fieldValue = getFieldValue(obj, fieldName);
				if (map.containsKey(fieldValue)) {
					map.get(fieldValue).add(obj);
					continue;
				}
				list = new ArrayList<Object>();
				list.add(obj);
				map.put(fieldValue, list);
			} catch (Exception e) {

			}
		}
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	/**
	 * List转为Map。fieldName作为Key，对象作为Value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		List<BeanEntity> entitys = PropertUtil.getBeanFields(obj);
		for (BeanEntity entity : entitys) {
			if (StringUtil.isNullOrEmpty(entity.getFieldValue())) {
				continue;
			}
			map.put(entity.getFieldName(), entity.getFieldValue());
		}
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	/**
	 * List转为Map。fieldName作为Key，对象作为Value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static Map<?, ?> listToMap(List<?> objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, Object> map = new TreeMap<Object, Object>();
		for (Object obj : objs) {
			try {
				Object fieldValue = getFieldValue(obj, fieldName);
				map.put(fieldValue, obj);
			} catch (Exception e) {

			}
		}
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	public static List<Method> loadMethods(Class<?> clazz) {
		List<Method> methods = methodMap.get(clazz);
		if (!StringUtil.isNullOrEmpty(methods)) {
			return methods;
		}
		methods = new ArrayList<Method>(Arrays.<Method>asList(clazz.getDeclaredMethods()));
		if (!StringUtil.isNullOrEmpty(clazz.getSuperclass())) {
			methods.addAll(loadMethods(clazz.getSuperclass()));
		}
		methodMap.put(clazz, methods);
		return methods;
	}

	/**
	 * 加载枚举的信息
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T loadEnumByField(Class<T> clazz, String fieldName, Object value) {
		if (!clazz.isEnum()) {
			throw new InvalidParameterException();
		}
		try {
			T[] enumConstants = clazz.getEnumConstants();
			for (T ec : enumConstants) {
				Object currValue = getFieldValue(ec, fieldName);
				if (value == currValue || currValue.equals(value)) {
					return ec;
				}
			}
			return null;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 加载枚举的信息
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String, Record> loadEnumRecord(Class<T> clazz) {
		if (!clazz.isEnum()) {
			throw new InvalidParameterException();
		}
		try {
			T[] enumConstants = clazz.getEnumConstants();
			Field[] fields = clazz.getDeclaredFields();
			if (StringUtil.isNullOrEmpty(fields)) {
				return null;
			}
			List<Field> fieldList = new ArrayList<Field>();
			for (Field field : fields) {
				try {
					if (!(clazz.isAssignableFrom(field.getType()))
							&& !(("[L" + clazz.getName() + ";").equals(field.getType().getName()))) {
						fieldList.add(field);
					}
				} catch (Exception e) {
				}
			}
			if (StringUtil.isNullOrEmpty(fieldList)) {
				return null;
			}
			Map<String, Record> records = new HashMap<String, Record>();
			for (T ec : enumConstants) {
				Record record = new Record();
				for (Field field : fieldList) {
					Object value = getFieldValue(ec, field.getName());
					record.put(field.getName(), value);
				}
				records.put(ec.toString(), record);
			}
			return records;
		} catch (Exception e) {
		}
		return null;
	}

	public static void setEnumFieldName(Class<?> clazz, String fieldName, String newFieldName) {
		if (!clazz.isEnum()) {
			throw new InvalidParameterException();
		}
		if (StringUtil.hasNull(fieldName, newFieldName)) {
			return;
		}
		try {
			Object[] enumConstants = clazz.getEnumConstants();
			Field[] fields = clazz.getDeclaredFields();
			if (StringUtil.isNullOrEmpty(fields)) {
				return;
			}
			List<Field> fieldList = new ArrayList<Field>();
			for (Field field : fields) {
				try {
					if (!(clazz.isAssignableFrom(field.getType()))
							&& !(("[L" + clazz.getName() + ";").equals(field.getType().getName()))) {
						fieldList.add(field);
					}
				} catch (Exception e) {
				}
			}
			if (StringUtil.isNullOrEmpty(fieldList)) {
				return;
			}
			for (Object ec : enumConstants) {
				if (!ec.toString().equals(fieldName)) {
					continue;
				}
				setFieldValue(ec, "name", newFieldName);
			}
			return;
		} catch (Exception e) {

		}
		return;
	}

	public static void setEnumValue(Class<?> clazz, String fieldName, Map<String, Object> valueMaps) {
		if (!clazz.isEnum()) {
			throw new InvalidParameterException();
		}
		if (StringUtil.isNullOrEmpty(valueMaps)) {
			return;
		}
		try {
			Object[] enumConstants = clazz.getEnumConstants();
			Field[] fields = clazz.getDeclaredFields();
			if (StringUtil.isNullOrEmpty(fields)) {
				return;
			}
			List<Field> fieldList = new ArrayList<Field>();
			for (Field field : fields) {
				try {
					if (!(clazz.isAssignableFrom(field.getType()))
							&& !(("[L" + clazz.getName() + ";").equals(field.getType().getName()))) {
						fieldList.add(field);
					}
				} catch (Exception e) {
				}
			}
			if (StringUtil.isNullOrEmpty(fieldList)) {
				return;
			}
			for (Object ec : enumConstants) {
				if (!ec.toString().equals(fieldName)) {
					continue;
				}
				for (Field field : fieldList) {
					for (String key : valueMaps.keySet()) {
						if (!key.equals(field.getName())) {
							continue;
						}
						setFieldValue(ec, field, valueMaps.get(key));
					}
				}
			}
			return;
		} catch (Exception e) {

		}
		return;
	}

	/**
	 * 获取class的字段列表
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Field> loadFields(Class<?> clazz) {
		List<Field> fields = fieldMap.get(clazz);
		if (!StringUtil.isNullOrEmpty(fields)) {
			return fields;
		}
		fields = new ArrayList<Field>();
		Field[] fieldArgs = clazz.getDeclaredFields();
		for (Field f : fieldArgs) {
			fields.add(f);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			fields.addAll(loadFields(superClass));
		}
		fieldMap.put(clazz, fields);
		return fields;
	}

	/**
	 * 将对象某些字段置空
	 * 
	 * @param obj
	 * @param fieldNames
	 */
	public static void removeFields(Object obj, String... fieldNames) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return;
		}
		List<BeanEntity> fields = PropertUtil.getBeanFields(obj);
		Map<String, BeanEntity> map = (Map<String, BeanEntity>) listToMap(fields, "fieldName");
		for (String tmp : fieldNames) {
			try {
				if (map.containsKey(tmp)) {
					BeanEntity entity = map.get(tmp);
					PropertUtil.setProperties(obj, entity.getFieldName(), null);
				}
			} catch (Exception e) {

			}

		}
	}

	/**
	 * 清理其余字段，仅保留对象某些字段
	 * 
	 * @param obj
	 * @param fieldNames
	 */
	public static void accepFields(Object obj, String... fieldNames) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return;
		}
		List<BeanEntity> fields = PropertUtil.getBeanFields(obj);
		Map<String, BeanEntity> map = (Map<String, BeanEntity>) listToMap(fields, "fieldName");
		for (String tmp : fieldNames) {
			try {
				if (!map.containsKey(tmp)) {
					BeanEntity entity = map.get(tmp);
					PropertUtil.setProperties(obj, entity.getFieldName(), null);
				}
			} catch (Exception e) {

			}

		}
	}

	/**
	 * value值转换为对应的类型
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseValue(Object value, Class<?> clazz) {
		try {
			if (value == null) {
				if (clazz.isPrimitive()) {
					if (boolean.class.isAssignableFrom(clazz)) {
						return false;
					}
					if (byte.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (char.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (short.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (int.class.isAssignableFrom(clazz)) {
						return 0;
					}
					if (float.class.isAssignableFrom(clazz)) {
						return 0f;
					}
					if (long.class.isAssignableFrom(clazz)) {
						return 0l;
					}
					if (double.class.isAssignableFrom(clazz)) {
						return 0d;
					}
				}
				return value;
			}
			if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) {
				value = Integer.valueOf(value.toString());
				return value;
			}
			if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
				value = Float.valueOf(value.toString());
				return value;
			}
			if (Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
				value = Long.valueOf(value.toString());
				return value;
			}
			if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) {
				value = Double.valueOf(value.toString());
				return value;
			}
			if (Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz)) {
				value = Short.valueOf(value.toString());
				return value;
			}
			if (Byte.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz)) {
				value = Byte.valueOf(value.toString());
				return value;
			}
			if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
				value = ("true".equals(value.toString()) || "1".equals(value.toString())) ? true : false;
				return value;
			}
			if (String.class.isAssignableFrom(clazz)) {
				value = value.toString();
				return value;
			}
			if (Date.class.isAssignableFrom(clazz)) {
				value = DateUtils.toDate(value);
				return value;
			}
			return value;
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * 获取方法参数列表
	 * 
	 * @param method
	 * @return
	 */
	public static List<BeanEntity> getMethodParas(Method method) {
		try {
			if (paramMap.containsKey(method)) {
				return paramMap.get(method);
			}
			Class<?>[] types = method.getParameterTypes();
			if (StringUtil.isNullOrEmpty(types)) {
				return null;
			}
			List<String> paraNames = getMethodParaNames(method);
			if (StringUtil.isNullOrEmpty(paraNames)) {
				return null;
			}
			Annotation[][] paraAnnotations = method.getParameterAnnotations();
			List<BeanEntity> entitys = new ArrayList<BeanEntity>();
			for (int i = 0; i < paraNames.size(); i++) {
				BeanEntity entity = new BeanEntity();
				entity.setFieldName(paraNames.get(i));
				entity.setFieldAnnotations(paraAnnotations[i]);
				entity.setFieldType(types[i]);
				entitys.add(entity);
			}
			paramMap.put(method, entitys);
			return entitys;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * 获取方法的参数名
	 * </p>
	 * 
	 * @param m
	 * @return
	 */
	public static List<String> getMethodParaNames(Method method) {
		if (StringUtil.isNullOrEmpty(method.getParameters())) {
			return null;
		}
		List<String> paramNames = new ArrayList<String>();
		for (Parameter parameter : method.getParameters()) {
			paramNames.add(parameter.getName());
		}
		return paramNames;
	}

}