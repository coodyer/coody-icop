package org.coody.framework.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.coody.framework.util.PropertUtil;

public class Record implements Map<String, Object> {
	private Map<String, Object> map = new HashMap<String, Object>();

	public Record() {
	}

	public Record(Map<String, Object> map) {
		if (map == null) {
			return;
		}
		this.map = map;
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Collection<Object> values() {
		return map.values();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public void clear() {
		map.clear();
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public Object parsBean(Class<?> cla) {
		return PropertUtil.mapToModel(map,cla);
	}
}

    