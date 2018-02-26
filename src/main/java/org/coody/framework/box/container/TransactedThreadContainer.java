package org.coody.framework.box.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.coody.framework.util.StringUtil;


@SuppressWarnings({  "unchecked" })
public class TransactedThreadContainer {

	
	
	public static ThreadLocal<Map<String, Object>> threadTransactedContainer = new ThreadLocal<Map<String, Object>>();
	
	/**
	 * 是否需要事物
	 */
	private static final String needTransacted="needTransacted";
	
	/**
	 * connection容器
	 */
	private static final String connectionContainer="connectionContainer";
	
	/**
	 * 判断是否存在事物控制
	 * @return
	 */
	public static boolean hasTransacted(){
		
		Map<String, Object> threadContainer=threadTransactedContainer.get();
		if(StringUtil.isNullOrEmpty(threadContainer)){
			return false;
		}
		Boolean needTransacteder=(Boolean) threadContainer.get(needTransacted);
		if(needTransacteder==null){
			return false;
		}
		return needTransacteder;
	}
	
	public static void writeHasTransacted(){
		Map<String, Object> threadContainer=threadTransactedContainer.get();
		if(StringUtil.isNullOrEmpty(threadContainer)){
			threadContainer=new HashMap<String, Object>();
		}
		threadContainer.put(needTransacted, true);
		threadTransactedContainer.set(threadContainer);
	}
	
	public static void writeDataSource(DataSource source,Connection connection){
		Map<String, Object> threadContainer=threadTransactedContainer.get();
		if(StringUtil.isNullOrEmpty(threadContainer)){
			threadContainer=new HashMap<String, Object>();
		}
		Map<DataSource, Connection> connectionMap=(Map<DataSource, Connection>) threadContainer.get(connectionContainer);
		if(StringUtil.isNullOrEmpty(connectionMap)){
			connectionMap=new HashMap<DataSource, Connection>();
		}
		connectionMap.put(source, connection);
		threadContainer.put(connectionContainer, connectionMap);
		threadTransactedContainer.set(threadContainer);
	}
	
	/**
	 * 从线程容器获取连接
	 * @param source
	 * @return
	 */
	public static Connection getConnection(DataSource source){
		Map<String, Object> threadContainer=threadTransactedContainer.get();
		if(StringUtil.isNullOrEmpty(threadContainer)){
			return null;
		}
		Map<DataSource, Connection> connectionMap=(Map<DataSource, Connection>) threadContainer.get(connectionContainer);
		if(StringUtil.isNullOrEmpty(connectionMap)){
			connectionMap=new HashMap<DataSource, Connection>();
		}
		return connectionMap.get(source);
	}
	
	/**
	 * 从线程容器获取所有连接
	 * @param source
	 * @return
	 */
	public static List<Connection> getConnections(){
		Map<String, Object> threadContainer=threadTransactedContainer.get();
		if(StringUtil.isNullOrEmpty(threadContainer)){
			return null;
		}
		Map<DataSource, Connection> connectionMap=(Map<DataSource, Connection>) threadContainer.get(connectionContainer);
		if(StringUtil.isNullOrEmpty(connectionMap)){
			connectionMap=new HashMap<DataSource, Connection>();
		}
		return new ArrayList<Connection>(connectionMap.values());
	}
}
