package org.coody.web.dao;

import java.util.List;

import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.annotation.OutBean;
import org.coody.web.comm.base.JdbcTemplate;
import org.coody.web.domain.IcopTest;

@InitBean
public class IcopDao {

	@OutBean
	JdbcTemplate jdbcTemplate;
	
	public IcopTest getIcop(Integer id){
		return jdbcTemplate.findBeanFirst(IcopTest.class,"id",id);
	}
	
	
	public List<IcopTest> getIcops(){
		return jdbcTemplate.findBean(IcopTest.class);
	}
	
	public Long delIcop(Integer id){
		
		String sql="delete from icop_test where id=? limit 1";
		return jdbcTemplate.doUpdate(sql,id);
	}
}
