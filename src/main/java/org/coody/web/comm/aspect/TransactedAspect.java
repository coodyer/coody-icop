package org.coody.web.comm.aspect;

import java.sql.Connection;
import java.util.List;

import org.coody.framework.box.annotation.Around;
import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.annotation.Transacted;
import org.coody.framework.box.container.TransactedThreadContainer;
import org.coody.framework.box.wrapper.AspectWrapper;
import org.coody.framework.util.StringUtil;

@InitBean
public class TransactedAspect {

	/**
	 * 事物控制
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(Transacted.class)
	public Object transacted(AspectWrapper wrapper) throws Throwable{
		
		try{
			TransactedThreadContainer.writeHasTransacted();
			Object result= wrapper.invoke();
			//提交事物
			List<Connection> connections=TransactedThreadContainer.getConnections();
			if(!StringUtil.isNullOrEmpty(connections)){
				for(Connection conn:connections){
					try{
						conn.commit();
					}catch (Exception e) {
					}
				}
			}
			return result;
		}finally {
			//关闭连接
			List<Connection> connections=TransactedThreadContainer.getConnections();
			if(!StringUtil.isNullOrEmpty(connections)){
				for(Connection conn:connections){
					try{
						conn.close();
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
	}
}
