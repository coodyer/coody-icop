package org.coody.web.domain;

import org.coody.framework.entity.BaseModel;

@SuppressWarnings("serial")
public class UserInfo extends BaseModel{

	private String userId;
	
	private String userName;
	
	private Integer age;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	
	
	
}
