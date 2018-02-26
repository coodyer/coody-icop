package org.coody.web.domain;

import java.util.Date;

import org.coody.framework.entity.BaseModel;

@SuppressWarnings("serial")
public class IcopTest extends BaseModel{

	
	private Integer id;
	private String name;
	private Integer age;
	private Date createTime;
	private Integer sex;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	
	
	

}
