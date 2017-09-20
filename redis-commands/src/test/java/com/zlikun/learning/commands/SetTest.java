package com.zlikun.learning.commands;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

/**
 * SET类型命令测试
 * @author	zlikun
 * @date	2017年9月20日 下午2:36:57
 */
public class SetTest extends TestBase {

	final String key = "zlikun:set" ;
	final String newkey = key + ":dst" ;

	@Override
	protected void init() {
		jedis.del(key ,newkey) ;
	}
	
	@Test
	public void test() {
		
		// 添加元素，返回添加后集合长度，重复元素将被过滤
		Assert.assertEquals(Long.valueOf(3), jedis.sadd(key, "A" ,"B" ,"C" ,"C"));

		// 查看集合元素个数
		Assert.assertEquals(Long.valueOf(3), jedis.scard(key));
		
		// 判断集合中是否包含指定元素
		Assert.assertTrue(jedis.sismember(key, "A"));
		Assert.assertFalse(jedis.sismember(key, "D"));
		
		// 获取集合中全部元素
		// [B, A, C]
		System.out.println(jedis.smembers(key));
	
		// 删除元素，返回成功删除的元素个数
		Assert.assertEquals(Long.valueOf(2), jedis.srem(key, "A" ,"C" ,"D"));
		
		// 移动集合中的元素到另一个集合
		Assert.assertEquals(Long.valueOf(1), jedis.smove(key, newkey, "B"));
		
		// []
		System.out.println(jedis.smembers(key));
		// [B]
		System.out.println(jedis.smembers(newkey));
		
		// 重新初始化集合
		jedis.sadd(key, "A" ,"B" ,"C" ,"D") ;
		
		// 移除并返回集合中的一个元素
		// D
		System.out.println(jedis.spop(key));
		
		// 返回集合中一个或多个随机数
		System.out.println(jedis.srandmember(key));
		System.out.println(jedis.srandmember(key ,2));
		
	}

	/**
	 * 集合间的运算
	 */
	@Test
	public void calc() {
		
		jedis.sadd(key, "A" ,"B" ,"C") ;
		jedis.sadd(newkey, "A" ,"B" ,"D") ;
		
		// 计算差值，前面减后面
		// [C]
		System.out.println(jedis.sdiff(key ,newkey));
		// [D]
		System.out.println(jedis.sdiff(newkey ,key));
		
		String dstkey = "dst" ;
		
		// 计算差值，并存储到新的集合中
		Assert.assertEquals(Long.valueOf(1), jedis.sdiffstore(dstkey, key ,newkey)) ;
		// [C]
		System.out.println(jedis.smembers(dstkey));
		jedis.del(dstkey) ;
		
		// 计算交集，与顺序无关
		// [B, A]
		System.out.println(jedis.sinter(key ,newkey));
		
		// 计算交集，并存储到新的集合中
		Assert.assertEquals(Long.valueOf(2), jedis.sinterstore(dstkey, key ,newkey)) ;
		// [A, B]
		System.out.println(jedis.smembers(dstkey));
		jedis.del(dstkey) ;
		
		// 计算并集，与顺序无关
		// [A, B, D, C]
		System.out.println(jedis.sunion(key ,newkey));
		
		// 计算并集，并存储到新的集合中
		Assert.assertEquals(Long.valueOf(4), jedis.sunionstore(dstkey, key ,newkey)) ;
		// [A, B, D, C]
		System.out.println(jedis.smembers(dstkey));
		jedis.del(dstkey) ;

	}
	
}
