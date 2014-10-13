package com.palm.memcached;

import java.util.Date;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * 
 * @author weixiang.qin
 * 
 */
public class MemcachedHelper {
	private static MemcachedHelper instance;
	private MemCachedClient client;
	private long expird = 60 * 1000;// 保存对象时间,默认为一分钟
	private String[] servers;// memcache server地址,包括端口号
	private Integer[] weights;// memcache 负载均衡实用的比重
	private int initConn;// 初始化连接数量
	private int minConn;// 最小连接数量
	private int maxConn;// 最大连接数量
	private int maxIdle;// 连接最大空闲时间
	private long maintSleep;// 维护线程执行休眠时间
	private boolean nagle; // Tcp的规则就是在发送一个包之前，本地机器会等待远程主机对上一次发送的包的确认信息到来；这个方法就可以关闭套接字的缓存，以至这个包准备好了就发；
	private int socketTO;// 读取数据超时时间
	private int socketConnectTO;// 建立连接超时时间

	private MemcachedHelper() {
		init();
	}

	public static synchronized MemcachedHelper getInstance() {
		if (instance == null) {
			instance = new MemcachedHelper();
		}
		return instance;
	}

	public void init() {
		try {
			AbstractConfiguration.setDefaultListDelimiter(',');// 默认分割符
			Configuration configuration = new PropertiesConfiguration(
					"memcached.properties");
			servers = configuration.getStringArray("servers");
			String[] weightStrs = configuration.getStringArray("weights");
			weights = new Integer[weightStrs.length];
			for (int i = 0; i < weightStrs.length; i++) {
				weights[i] = Integer.parseInt(weightStrs[i]);
			}
			initConn = configuration.getInt("initConn");
			minConn = configuration.getInt("minConn");
			maxConn = configuration.getInt("maxConn");
			maxIdle = configuration.getInt("maxIdle");
			maintSleep = configuration.getInt("maintSleep");
			nagle = configuration.getBoolean("nagle");
			socketTO = configuration.getInt("socketTO");
			socketConnectTO = configuration.getInt("socketConnectTO");
			client = new MemCachedClient();
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(servers);
			pool.setWeights(weights);
			pool.setInitConn(initConn);
			pool.setMinConn(minConn);
			pool.setMaxConn(maxConn);
			pool.setMaxIdle(maxIdle);
			pool.setMaintSleep(maintSleep);
			pool.setNagle(nagle);
			pool.setSocketTO(socketTO);
			pool.setSocketConnectTO(socketConnectTO);
			pool.initialize();
			client.setDefaultEncoding("UTF-8");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向缓存添加一个对象
	 * 
	 * @param key
	 * @param value
	 */
	public void add(String key, Object value) {
		if (client.keyExists(key)) {
			client.set(key, value, new Date(expird));
		} else {
			client.add(key, value, new Date(expird));
		}
	}

	/**
	 * 向缓存添加一个对象
	 * 
	 * @param key
	 * @param value
	 * @param expird
	 *            过期时间(毫秒)
	 */
	public void add(String key, Object value, long expird) {
		if (client.keyExists(key)) {
			client.set(key, value, new Date(expird));
		} else {
			client.add(key, value, new Date(expird));
		}
	}

	/**
	 * key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean keyExists(String key) {
		return client.keyExists(key);
	}

	/**
	 * 替换
	 * 
	 * @param key
	 * @param value
	 */
	public void replace(String key, Object value) {
		client.replace(key, value);
	}

	/**
	 * 替换
	 * 
	 * @param key
	 * @param value
	 * @param expird
	 */
	public void replace(String key, Object value, long expird) {
		client.replace(key, value, new Date(expird));
	}

	/**
	 * 根据key读取一个对象
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return client.get(key);
	}

	/**
	 * 读取并删除
	 * 
	 * @param key
	 * @return
	 */
	public Object getremove(String key) {
		Object object = client.get(key);
		client.delete(key);
		return object;
	}

	/**
	 * 删除一个对象
	 * 
	 * @param key
	 */
	public void remove(String key) {
		client.delete(key);
	}

	/**
	 * 删除所有对象
	 */
	public void removeall() {
		client.flushAll();
	}
}
