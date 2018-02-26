package org.coody.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coody.framework.box.container.BeanContainer;
import org.coody.web.domain.UserInfo;
import org.coody.web.service.UserService;

import com.alibaba.fastjson.JSON;

/**
 * 加载用户列表
 * 
 */
@SuppressWarnings("serial")
public class LoadUserList extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 获取Bean
		UserService userService=BeanContainer.getBean(UserService.class);
		List<UserInfo> userlist=userService.getUsers();
		response.getWriter().print(JSON.toJSONString(userlist));
	}

}
