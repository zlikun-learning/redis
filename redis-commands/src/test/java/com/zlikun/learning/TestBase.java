package com.zlikun.learning;

import org.junit.After;
import org.junit.Before;

import redis.clients.jedis.Jedis;

public abstract class TestBase {

	protected Jedis jedis;

	private String host = "redis.zlikun.com";
	private int port = 6379;

	@Before
	public void _init() {
		jedis = new Jedis(host, port);
		init();
	}

	@After
	public void _destroy() {
		destroy() ;
		if (jedis != null && !jedis.isConnected()) {
			jedis.close();
		}
	}

	/**
	 * 准备操作
	 */
	protected void init() {
		
	}
	
	/**
	 * 销毁操作
	 */
	protected void destroy() {
		
	}
	
}
