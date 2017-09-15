package com.zlikun.learning.commands;

import org.junit.After;
import org.junit.Before;

import redis.clients.jedis.Jedis;

public abstract class TestBase {

	protected Jedis jedis;

	private String host = "192.168.9.205";
	private int port = 6379;

	@Before
	public void init() {

		jedis = new Jedis(host, port);

	}

	@After
	public void destroy() {
		if (jedis != null && !jedis.isConnected()) {
			jedis.close();
		}
	}

}
