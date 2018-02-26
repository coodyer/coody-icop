package org.coody.web.service;

import java.util.List;

import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.annotation.OutBean;
import org.coody.web.comm.annotation.CacheWipe;
import org.coody.web.comm.annotation.CacheWrite;
import org.coody.web.comm.constant.CacheFinal;
import org.coody.web.dao.UserDao;
import org.coody.web.domain.UserInfo;

@InitBean
public class UserService {

	@OutBean
	UserDao userDao;
	
	
	/**
	 * 保存或更新用户信息
	 * @param user
	 */
	@CacheWipe(key=CacheFinal.USER_INFO,fields="user.userId")
	@CacheWipe(key=CacheFinal.USER_LIST)
	public void saveOrUpdateUser(UserInfo user){
		userDao.saveOrUpdateUser(user);
	}
	
	/**
	 * 查询用户列表
	 */
	@CacheWrite(key=CacheFinal.USER_LIST,validTime=3600)
	public List<UserInfo> getUsers(){
		return userDao.getUsers();
	}
	
	/**
	 * 删除用户
	 * @param userId
	 */
	@CacheWipe(key=CacheFinal.USER_INFO,fields="user.userId")
	@CacheWipe(key=CacheFinal.USER_LIST)
	public void deleteUser(String userId){
		userDao.deleteUser(userId);
	}
	
	/**
	 * 查询用户信息
	 */
	@CacheWrite(key=CacheFinal.USER_INFO,fields="userId")
	public UserInfo getUserInfo(String userId){
		return userDao.getUserInfo(userId);
	}
}
