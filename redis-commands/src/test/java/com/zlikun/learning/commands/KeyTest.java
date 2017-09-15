package com.zlikun.learning.commands;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * 键操作命令
 * http://www.redis.net.cn/order/
 * @author	zlikun
 * @date	2017年9月15日 下午5:44:49
 */
@Slf4j
public class KeyTest extends TestBase {

	@Test
	public void data() {
		// 准备测试数据
		jedis.set("key1" ,"A") ;
		jedis.set("key2" ,"B") ;
		jedis.set("key3" ,"C") ;
	}
	
	@Test
	public void del() {
		// 删除键
		log.info("计删除{}个键" ,jedis.del("key1" ,"key2" ,"key3"));
	}
	
	@Test
	public void dump() {
		// 序列化给定的Key，返回序列化的值
		Stream.of(jedis.dump("key1")).forEach(System.out::print);
	}
	
	@Test
	public void exists() {
		Assert.assertTrue(jedis.exists("key1"));
	}
	
	
	
}
