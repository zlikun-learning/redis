package com.zlikun.learning.commands;

import org.junit.Test;

import com.zlikun.learning.TestBase;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis配置相关命令
 * @author	zlikun
 * @date	2017年9月15日 下午5:27:11
 */
@Slf4j
public class RedisTest extends TestBase {

	@Test
	public void client_list() {
		// 获取连接到服务器的客户端连接列表
		log.info(jedis.clientList()) ;
	}
	
	@Test
	public void client_kill() {
		// 关闭客户端连接(ip:port)
		log.info(jedis.clientKill("192.168.9.180:34241")) ;	// OK
	}
	
	@Test
	public void client_getname() {
		// 获取连接的名称
		log.info(jedis.clientGetname());
	}
	
	@Test
	public void time() {
		// 返回当前服务器时间

		// 1505468005
		// 201244
		jedis.time().stream().forEach(log::info) ;
	}
	
	@Test
	public void command_info() {
		// 查看指定命令描述信息(查了好几个命令，啥也没返回?)
		log.info(jedis.info("EVAL"));
	}
	
	@Test
	public void config_get() {
		// 获取指定配置参数的值
		jedis.configGet("used_memory").stream().forEach(log::info);
	}
	
	@Test
	public void info() {
		// 获取 Redis 服务器的各种信息和统计数值
		log.info(jedis.info());
		// 指定信息区块
		log.info(jedis.info("Memory"));
	}
	
	@Test
	public void showlog() {
		// XXX jedis 不支持
	}
	
}
