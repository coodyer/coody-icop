package org.coody.web.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.iface.InitFace;
import org.coody.framework.util.JUUIDUtil;
import org.coody.framework.util.StringUtil;
import org.coody.web.domain.UserInfo;

@InitBean
public class UserDao implements InitFace{

	private static final Map<String, UserInfo> dataMap=new ConcurrentHashMap<String, UserInfo>();
	
	/**
	 * 保存或更新用户信息
	 * @param user
	 */
	public void saveOrUpdateUser(UserInfo user){
		if(StringUtil.isNullOrEmpty(user.getUserId())){
			user.setUserId(JUUIDUtil.createUuid());
			dataMap.put(user.getUserId(), user);
			return;
		}
		dataMap.put(user.getUserId(), user);
	}
	
	/**
	 * 查询用户列表
	 */
	public List<UserInfo> getUsers(){
		Collection<UserInfo> users=dataMap.values();
		return new ArrayList<UserInfo>(users);
	}
	/**
	 * 查询用户信息
	 */
	public UserInfo getUserInfo(String userId){
		return dataMap.get(userId);
	}
	/**
	 * 删除用户
	 * @param userId
	 */
	public void deleteUser(String userId){
		dataMap.remove(userId);
	}

	/**
	 * bean加载时执行
	 */
	public void init() {
		UserInfo user=new UserInfo();
		user.setAge(18);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("张三");
		dataMap.put(user.getUserId(), user);
		user=new UserInfo();
		user.setAge(19);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("李四");
		dataMap.put(user.getUserId(), user);
		user=new UserInfo();
		user.setAge(20);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("王五");
		dataMap.put(user.getUserId(), user);
		user=new UserInfo();
		user.setAge(21);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("马六");
		dataMap.put(user.getUserId(), user);
	}
}
