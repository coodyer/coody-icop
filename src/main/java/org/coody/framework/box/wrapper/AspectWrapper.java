package org.coody.framework.box.wrapper;

import java.lang.reflect.Method;

import org.coody.framework.entity.BaseModel;

import net.sf.cglib.proxy.MethodProxy;

@SuppressWarnings("serial")
public class AspectWrapper extends BaseModel{
	
	private Object bean;
	private Method method;
	private MethodProxy proxy;
	private Class<?> clazz;
	private Object[] params;
	private Method currentAspectMethod;
	private AspectWrapper childAspect;
	
	
	
	
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public Method getCurrentAspectMethod() {
		return currentAspectMethod;
	}
	public void setCurrentAspectMethod(Method currentAspectMethod) {
		this.currentAspectMethod = currentAspectMethod;
	}
	public AspectWrapper getChildAspect() {
		return childAspect;
	}
	public void setChildAspect(AspectWrapper childAspect) {
		this.childAspect = childAspect;
	}
	public Object getBean() {
		return bean;
	}
	public void setBean(Object bean) {
		this.bean = bean;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public MethodProxy getProxy() {
		return proxy;
	}
	public void setProxy(MethodProxy proxy) {
		this.proxy = proxy;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	
	public Object invoke() throws Throwable{
		if(childAspect!=null){
			return childAspect.getCurrentAspectMethod().invoke(bean, childAspect);
		}
		return proxy.invokeSuper(bean, params);
	}
}
