package com.zlikun.learning.commands;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

/**
 * Hash类型命令测试
 * @author	zlikun
 * @date	2017年9月20日 下午2:15:04
 */
public class HashTest extends TestBase {

	final String key = "zlikun:hash" ;

	@Override
	protected void init() {
		jedis.del(key) ;
	}
	
	@Test
	public void test() {
		
		// 添加一个字段
		Assert.assertEquals(Long.valueOf(1), jedis.hset(key, "name", "zlikun")) ;
		
		// 添加多个字段(Map<String ,String>)
		Map<String ,String> data = new HashMap<>(4) ;
		data.put("age", "120") ;
		data.put("ctime", "2017-9-1 12:27:00") ;
		Assert.assertEquals("OK", jedis.hmset(key, data)) ;
		
		// 查询字段数量
		Assert.assertEquals(Long.valueOf(3), jedis.hlen(key)) ;
		
		// 判断字段是否存在
		Assert.assertTrue(jedis.hexists(key, "name"));
		Assert.assertFalse(jedis.hexists(key, "salary"));
		
		// 字段自增(负数表示自减)
		Assert.assertEquals(Long.valueOf(1200) ,jedis.hincrBy(key, "salary", 1200));
		Assert.assertEquals(Long.valueOf(1150) ,jedis.hincrBy(key, "salary", -50));
		
		// 设置字段(针对不存在字段，存在则无效)
		Assert.assertEquals(Long.valueOf(0) ,jedis.hsetnx(key, "salary", "6000"));
		Assert.assertEquals(Long.valueOf(1) ,jedis.hsetnx(key, "hobbies", "游戏|摄影|编程"));
	
		// 获取字段名列表
		// [name, ctime, salary, hobbies, age]
		System.out.println(jedis.hkeys(key));
		
		// 获取值列表
		// [zlikun, 2017-9-1 12:27:00, 120, 1150, 游戏|摄影|编程]
		System.out.println(jedis.hvals(key));
		
		// 获取一个字段信息
		Assert.assertEquals("zlikun", jedis.hget(key, "name")) ;
		// 获取多个字段信息
		// [zlikun, 1150, 2017-9-1 12:27:00]
		System.out.println(jedis.hmget(key, "name" ,"salary" ,"ctime"));
		// 获取全部字段信息
		// {name=zlikun, ctime=2017-9-1 12:27:00, salary=1150, hobbies=游戏|摄影|编程, age=120}
		System.out.println(jedis.hgetAll(key));
		
		// 删除字段，可以一次删除多个
		Assert.assertEquals(Long.valueOf(2) ,jedis.hdel(key, "salary" ,"hobbies"));
		
		// HSCAN 命令(略)
	}
	
}
