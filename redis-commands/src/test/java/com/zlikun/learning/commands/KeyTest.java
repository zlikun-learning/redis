package com.zlikun.learning.commands;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

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
	
	@Test
	public void expire() {
		final String key = UUID.randomUUID().toString() ;

		// 未设置过期时间(永久缓存)
		jedis.set(key, "A") ;
		Assert.assertEquals(Long.valueOf(-1), jedis.ttl(key));
		
		// 指定过期时间，相对于当前时间
		jedis.expire(key, 30) ;
		Assert.assertTrue(jedis.ttl(key) > 0);
		Assert.assertTrue(jedis.ttl(key) <= 30);
		
		// 通过设定一个过去的时间点，使用缓存过期
		jedis.expireAt(key, 0) ;
		Assert.assertEquals(Long.valueOf(-2), jedis.ttl(key));
	}
	
	@Test
	public void ttl() {
		final String key = UUID.randomUUID().toString() ;
		
		// 永久缓存，剩余生存时间值为：-1
		jedis.set(key, "A") ;
		Assert.assertEquals(Long.valueOf(-1), jedis.ttl(key));
		
		// 指定过期时间为当前剩余秒(毫秒)数
		jedis.setex(key, 30, "A") ;
		Assert.assertTrue(jedis.ttl(key) > 0);
		// TTL，单位：秒
		Assert.assertTrue(jedis.ttl(key) <= 30);
		// PTTL，单位：毫秒
		Assert.assertTrue(jedis.pttl(key) <= 30 * 1000);
		
		// 不存在的键剩余生存时间值为：-2
		jedis.del(key) ;
		Assert.assertEquals(Long.valueOf(-2), jedis.ttl(key));
	
	}
	
	@Test
	public void rename() {
		final String key = UUID.randomUUID().toString() ;
		jedis.setex(key, 30, "A") ;
		jedis.rename(key, "newkey") ;
		Assert.assertFalse(jedis.exists(key));
		Assert.assertEquals(Long.valueOf(30), jedis.ttl("newkey"));
	}
	
	@Test
	public void move() {
		// 选择索引为2的DB
		jedis.select(2) ;
		final String key = UUID.randomUUID().toString() ;
		jedis.setex(key, 30 ,"A") ;
		// 切换到索引为0的DB，数据不存在
		jedis.select(0) ;
		Assert.assertFalse(jedis.exists(key));
		// 将其移动到0
		jedis.select(2) ;
		jedis.move(key, 0) ;
		jedis.select(0) ;
		Assert.assertTrue(jedis.exists(key));
	}
	
}
