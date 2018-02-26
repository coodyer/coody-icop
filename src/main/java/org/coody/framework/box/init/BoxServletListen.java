package org.coody.framework.box.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.coody.framework.util.StringUtil;

public class BoxServletListen implements ServletContextListener {

	Logger logger = Logger.getLogger(BoxServletListen.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("运行contextDestroyed");
	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			String packet = event.getServletContext().getInitParameter("scanPacket");
			if (StringUtil.isNullOrEmpty(packet)) {
				logger.error("启动参数:scanPacket为空");
				return;
			}
			String[] packets = packet.split(",");
			BoxRute.init(packets);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
