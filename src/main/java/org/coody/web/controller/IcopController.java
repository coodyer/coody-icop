package org.coody.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.coody.framework.box.annotation.JsonSerialize;
import org.coody.framework.box.annotation.OutBean;
import org.coody.framework.box.annotation.PathBinding;
import org.coody.framework.util.StringUtil;
import org.coody.web.comm.entity.MsgEntity;
import org.coody.web.domain.IcopTest;
import org.coody.web.service.IcopService;

@PathBinding("/icop")
public class IcopController {

	
	@OutBean
	IcopService icopService;
	
	@PathBinding("loadIcops.do")
	@JsonSerialize
	public Object loadIcops(){
		List<IcopTest> icops=icopService.getIcops();
		return icops;
	}
	/**
	 * 删除数据
	 * @param request
	 * @return
	 */
	@PathBinding("delIcop.do")
	@JsonSerialize
	public Object delIcop(HttpServletRequest request){
		Integer id=StringUtil.toInteger(request.getParameter("id"));
		Long code=icopService.delIcop(id);
		if(code>0){
			return new MsgEntity(0,"操作成功");
		}
		return new MsgEntity(-1,"系统出错");
	}
}
