package org.coody.web.comm.base;

import java.beans.PropertyVetoException;
import java.io.IOException;

import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.iface.InitFace;
import org.coody.framework.box.jdbc.JdbcHandle;

@InitBean
public class JdbcTemplate extends JdbcHandle implements InitFace{


	public void init() {
		try {
			initConfig("config/c3p0.properties");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

}
