package org.coody.framework.box.adapt;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.util.StringUtil;

/**
 * 参数适配器
 * @author admin
 *
 */
public class ParamsAdapt {

	
	public static Object[] adaptParams(Class<?> []paramTypes,Map<String,Object> paras,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		if(StringUtil.isNullOrEmpty(paramTypes)){
			return null;
		}
		if(StringUtil.isNullOrEmpty(paras)){
			Object [] params=new Object[paramTypes.length];
			for(int i=0;i<paramTypes.length;i++){
				Class<?> paraType=paramTypes[i];
				if(paraType.isAssignableFrom(request.getClass())){
					params[i]=request;
					continue;
				}
				if(paraType.isAssignableFrom(response.getClass())){
					params[i]=response;
					continue;
				}
				if(paraType.isAssignableFrom(session.getClass())){
					params[i]=session;
					continue;
				}
			}
			return params;
		}
		
		return new Object[paramTypes.length];
	} 
}
