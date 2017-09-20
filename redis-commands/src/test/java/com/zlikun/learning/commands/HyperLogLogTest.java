package com.zlikun.learning.commands;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;


/**
 * HyperLogLog数据结构
 * @author	zlikun
 * @date	2017年9月20日 下午5:43:15
 */
public class HyperLogLogTest extends TestBase {

	final String key = "zlikun:HyperLogLog" ;
	final String newkey = key + ":new" ;

	@Override
	protected void init() {
		jedis.del(key ,newkey) ;
	}
	
	@Override
	protected void destroy() {
		jedis.del(key ,newkey) ;
	}
	
	@Test
	public void test() {
		
		// 添加指定元素到 HyperLogLog 中，返回基数估算值
		Assert.assertEquals(Long.valueOf(3), jedis.pfadd(key, "A" ,"B" ,"C" ,"A"));
		
		// 返回 HyperLogLog 的基数估算值
		Assert.assertEquals(3, jedis.pfcount(key));
		
		// 将多个 HyperLogLog 合并为一个 HyperLogLog
		Assert.assertEquals("OK", jedis.pfmerge(newkey, key)) ;
		Assert.assertEquals(3, jedis.pfcount(newkey));
		
	}
	
}
