package com.zlikun.learning.lua;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.zlikun.learning.TestBase;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Pipeline;

/**
 * 测试Lua脚本在ZSET命令上的使用
 * @author	zlikun
 * @date	2017年9月18日 上午11:48:56
 */
@Slf4j
public class ZSetLuaTest extends TestBase {

	private String key = "zlikun:zset" ;
	
	@Override
	protected void init() {
		jedis.del(key) ;
		jedis.zadd(key, 98.0, "zlikun") ;
		jedis.zadd(key, 67.5, "nginx") ;
		jedis.zadd(key, 62.0, "apache") ;
	}
	
	@Override
	protected void destroy() {
		jedis.del(key) ;
	}

	@Test
	public void test() {
		
		StringBuilder builder = new StringBuilder() ;
		// 传入键和当前时间戳(毫秒)，根据该值取出早于(等于)该值的元素
		builder.append("local key ,timestamp = KEYS[1] ,ARGV[1]").append("\n") ;
		builder.append("local result = redis.call('ZRANGEBYSCORE' ,key ,0 ,timestamp)").append("\n") ;
		// 将符合该条件的成员删除
		builder.append("redis.call('ZREMRANGEBYSCORE' ,key ,0 ,timestamp)").append("\n") ;
		// 返回结果集
		builder.append("return result").append("\n") ;
		
		String lua = builder.toString() ;
		
		String sha1 = jedis.scriptLoad(lua) ;
		Assert.assertEquals("e0c35a97284c52193820192d38a1b0e0b058e07b", sha1);
		
		// 测试该脚本，返回并删除集合中分数80及小于80的成员
		Object result = jedis.evalsha(sha1, 1, key ,"80") ;
		// java.util.ArrayList / [apache, nginx]
		log.info("{} / {}" ,result.getClass() ,result);
		
		// 检查元素是否删除
		Assert.assertEquals(Long.valueOf(1), jedis.zcard(key));
		// [zlikun]
		log.info("{}" ,jedis.zrange(key, 0, -1));
		
	}
	
	@Test @Ignore
	public void performance() {
		
		// 注册Lua脚本
		StringBuilder builder = new StringBuilder() ;
		// 传入键和当前时间戳(毫秒)，根据该值取出早于(等于)该值的元素
		builder.append("local key ,timestamp = KEYS[1] ,ARGV[1]").append("\n") ;
		builder.append("local result = redis.call('ZRANGEBYSCORE' ,key ,0 ,timestamp)").append("\n") ;
		// 将符合该条件的成员删除
		builder.append("redis.call('ZREMRANGEBYSCORE' ,key ,0 ,timestamp)").append("\n") ;
		// 返回结果集
		builder.append("return result").append("\n") ;
		String lua = builder.toString() ;
		String sha1 = jedis.scriptLoad(lua) ;

		// 准备100万条记录
		for(int i = 0 ;i < 200 ; i ++) {
			Pipeline pipe = jedis.pipelined() ;
			for(int j = 0 ; j < 1000 ;j ++) {
				pipe.zadd(key, i * j, "zlikun" + i * j) ;
			}
			pipe.sync();
		}
		
		long time = System.currentTimeMillis() ;
		System.err.println("程序开始执行：" + time);
		
		// 测试执行100,000时长
		for(int i = 0 ;i < 100 * 1000 ; i ++) {
			jedis.evalsha(sha1, 1, key ,"80") ;
		}
		
		System.err.println(String.format("程序执行耗时：%d 毫秒!", System.currentTimeMillis() - time));
		
	}
	
}
