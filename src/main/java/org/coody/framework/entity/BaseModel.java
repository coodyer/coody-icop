package org.coody.framework.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

import com.alibaba.fastjson.annotation.JSONField;

@SuppressWarnings("serial")
public class BaseModel implements Serializable{
	

	@SuppressWarnings("unchecked")
	@JSONField(serialize=false)  
	public <T> T initModel(){
		List<BeanEntity> beanEntitys=PropertUtil.getBeanFields(this);
		try {
			for(BeanEntity field:beanEntitys){
				if(!StringUtil.isNullOrEmpty(field.getFieldValue())){
					continue;
				}
				if(Number.class.isAssignableFrom(field.getFieldType())){
					PropertUtil.setProperties(this, field.getFieldName(),PropertUtil.parseValue(0, field.getFieldType()));
				}
				if(Date.class.isAssignableFrom(field.getFieldType())){
					PropertUtil.setProperties(this, field.getFieldName(), new Date() );
				}
				if(String.class.isAssignableFrom(field.getFieldType())){
					PropertUtil.setProperties(this, field.getFieldName(), "");
				}
			}
		} catch (Exception e) {
		}
		return (T) this;
	}
}
