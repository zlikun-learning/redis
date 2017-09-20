package com.zlikun.learning.commands;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

/**
 * 字符串类型命令
 * @author	zlikun
 * @date	2017年9月15日 下午5:44:38
 */
public class StringTest extends TestBase {

	final String key = "zlikun:string" ;
	final String key2 = "zlikun:string:2" ;

	@Override
	protected void init() {
		jedis.del(key) ;
		jedis.del(key2) ;
	}
	
	@Test
	public void test() {
		
		Assert.assertEquals("OK", jedis.set(key, "1")) ;
		Assert.assertEquals("1", jedis.get(key)) ;
		Assert.assertNull(jedis.get(key2)) ;

		Assert.assertEquals("OK", jedis.setex(key, 30, "1")) ;
		Assert.assertEquals(Long.valueOf(30), jedis.ttl(key)) ;
		
		// 写入一个已存在的Key，返回0，不存在返回1
		Assert.assertEquals(Long.valueOf(0), jedis.setnx(key, "lock")) ;
		Assert.assertEquals(Long.valueOf(1), jedis.setnx(key2, "lock")) ;
		
		// 同时获取、写入多个KEY
		// 1
		// lock
		jedis.mget(key ,key2).forEach(System.out::println) ;
		Assert.assertEquals("OK", jedis.mset(key ,"T" ,key2 ,"DEF")) ;
		// 返回写入成功的数量
		Assert.assertEquals(Long.valueOf(0), jedis.msetnx(key ,"T" ,key2 ,"DEF")) ;
		
		// 取出旧值，设置新值
		Assert.assertEquals("T", jedis.getSet(key, "ABC")) ;
		Assert.assertEquals("ABC", jedis.get(key)) ;
		
		// 获取字符串的子串
		Assert.assertEquals("BC", jedis.getrange(key, 1, -1)) ;
		// 从第二个字符开始替换为指定值，返回新字符串长度
		Assert.assertEquals(Long.valueOf(4), jedis.setrange(key, 1, "KVM")) ;
		Assert.assertEquals("AKVM", jedis.get(key)) ;
		
		// 追加字符串，返回新串长度
		Assert.assertEquals(Long.valueOf(6), jedis.append(key, "TC")) ;
		Assert.assertEquals("AKVMTC", jedis.get(key)) ;
		
		// 获取字符串值长度
		Assert.assertEquals(Long.valueOf(6), jedis.strlen(key));
		
		// 自增、自减
		jedis.set(key ,"0") ;
		Assert.assertEquals(Long.valueOf(1), jedis.incr(key));
		Assert.assertEquals(Long.valueOf(0), jedis.decr(key));
		Assert.assertEquals(Long.valueOf(5), jedis.incrBy(key ,5));
		Assert.assertEquals(Long.valueOf(2), jedis.decrBy(key ,3));
		
	}
	
}
