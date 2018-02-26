package org.coody.web.comm.enm;

import org.coody.framework.util.PropertUtil;

/**
 * 消息响应码枚举
 * 
 * @author deng
 *
 */
public enum RespEnum {

	SUCCESS(0, "操作成功"),// 成功标志
	COMMAND_ERROR(1,  "指令有误"),
	SYSTEM_ERROR(2,  "系统繁忙"),
	PARAM_ISERRPR(3,"参数有误"),
	NO_DATA(4,"暂无数据"),
	DATA_ERROR(5,"数据格式有误"),
	LOGIC_TABLE_NOTEXISTS(5,"逻辑表不存在"),
	WHERE_PARSE_ERROR(6,"条件解释有误"),
	OTHER(-1001,"其他错误"),
	;
	private int code;
	private String msg;

	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg=msg;
	}
	RespEnum(int code,  String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public static String getMsgByCode(Integer code){
		RespEnum enm=PropertUtil.loadEnumByField(RespEnum.class, "code", code);
		return enm.getMsg();
	}

}
