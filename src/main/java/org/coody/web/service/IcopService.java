package org.coody.web.service;

import java.util.List;

import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.annotation.OutBean;
import org.coody.framework.box.annotation.Transacted;
import org.coody.web.dao.IcopDao;
import org.coody.web.domain.IcopTest;

@InitBean
public class IcopService {

	@OutBean
	IcopDao icopDao;
	
	public IcopTest getIcop(Integer id){
		return icopDao.getIcop(id);
	}
	
	
	public List<IcopTest> getIcops(){
		return icopDao.getIcops()
				;
	}
	@Transacted
	public Long delIcop(Integer id){
		Long code= icopDao.delIcop(id);
		Integer i=50/0;
		System.out.println(i);
		return code;
	}
	
}
