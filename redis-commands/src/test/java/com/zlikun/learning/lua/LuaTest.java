package com.zlikun.learning.lua;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

/**
 * Redis + Lua
 * @author	zlikun
 * @date	2017年9月20日 下午7:17:15
 */
public class LuaTest extends TestBase {
	
	@Override
	protected void init() {
		jedis.del("zlikun:hash") ;
	}

	@Test
	public void test() {
		
		// 脚本将ARGV按2自增计数，如果不为偶数，则忽略最后一个值
		String script = "for i = 1 ,#ARGV ,2 do if ARGV[i + 1] then redis.call('HSET' ,KEYS[1] ,ARGV[i] ,ARGV[i + 1]) end end return redis.call('HGETALL' ,KEYS[1])" ;
		
		// 执行脚本，指定KEYS参数长度为1，剩下的为ARGV参数
		// [name, zlikun, age, 120]
		System.out.println(jedis.eval(script ,1 ,"zlikun:hash" ,"name" ,"zlikun" ,"age" ,"120" ,"ctime"));
		
		// 载入脚本
		String sha1 = jedis.scriptLoad(script) ;
		
		Assert.assertEquals("9b9725f79038b35171f3401b1a4ebdd0aea426ac", sha1);
		
		// 执行脚本(sha1)
		// [name, zlikun, age, 120]
		System.out.println(jedis.evalsha(sha1 ,1 ,"zlikun:hash" ,"name" ,"zlikun" ,"age" ,"120" ,"ctime")) ;
		
	}
	
}
