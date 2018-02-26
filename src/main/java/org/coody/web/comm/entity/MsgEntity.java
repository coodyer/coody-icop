package org.coody.web.comm.entity;

import java.util.HashMap;
import java.util.Map;

import org.coody.framework.entity.BaseModel;

/**
 * @remark 消息机制容器。
 * @author Coody
 * @email 644556636@qq.com
 * @blog 54sb.org
 */
@SuppressWarnings("serial")
public class MsgEntity extends BaseModel {

	public Integer code;
	public String msg;
	public Object datas;

	public MsgEntity(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public MsgEntity() {
	}

	public MsgEntity(Integer code, String msg, Object datas) {
		super();
		this.code = code;
		this.msg = msg;
		this.datas = datas;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setDataField(String fieldName,Object value){
		if(datas==null){
			datas=new HashMap<String,Object>();
		}
		if(!Map.class.isAssignableFrom(datas.getClass())){
			return;
		}
		try {
			((Map)datas).put(fieldName, value);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public Object getDatas() {
		return datas;
	}

	public void setDatas(Object datas) {
		this.datas = datas;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
