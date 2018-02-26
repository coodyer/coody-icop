package org.coody.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coody.framework.box.annotation.JsonSerialize;
import org.coody.framework.box.annotation.PathBinding;
import org.coody.web.comm.entity.MsgEntity;

import com.alibaba.fastjson.JSON;
/**
 * 测试Controller
 * @author admin
 *
 */
@PathBinding("/")
public class GeneralController {

	
	@PathBinding("/index.do")
	public Object index(HttpServletRequest request){
		System.out.println(JSON.toJSONString(request.getParameterMap()));
		return "index.jsp";
	}
	
	@PathBinding("/test.do")
	@JsonSerialize
	public Object test(HttpServletRequest request,HttpServletResponse response){
		System.out.println(JSON.toJSONString(request.getParameterMap()));
		return new MsgEntity(0,"操作成功","这是test的内容");
	}
}
