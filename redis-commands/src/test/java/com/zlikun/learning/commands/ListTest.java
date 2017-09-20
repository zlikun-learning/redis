package com.zlikun.learning.commands;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

import redis.clients.jedis.BinaryClient.LIST_POSITION;

/**
 * List类型命令
 * @author	zlikun
 * @date	2017年9月20日 上午11:44:38
 */
public class ListTest extends TestBase {

	final String key = "zlikun:list" ;
	final String newkey = key + ":dst" ;

	@Override
	protected void init() {
		jedis.del(key ,newkey) ;
	}
	
	@Test
	public void test() {
	
		// 从队列左边添加元素，返回队列长度
		Assert.assertEquals(Long.valueOf(3), jedis.lpush(key, "A" ,"B" ,"C")) ;

		// [C, B, A]
		System.out.println(jedis.lrange(key, 0, -1));

		// 从队列右边添加元素
		Assert.assertEquals(Long.valueOf(4), jedis.rpush(key, "D")) ;
		
		// [C, B, A, D]
		System.out.println(jedis.lrange(key, 0, -1));
		
		// 查询队列长度
		Assert.assertEquals(Long.valueOf(4), jedis.llen(key));
		
		// 向已存在队列头部添加元素，队列不存在时则无效
		// API允许添加多个值，但实际测试不行，只能添加一个
		Assert.assertEquals(Long.valueOf(5), jedis.lpushx(key, "O")) ;
		Assert.assertEquals(Long.valueOf(0), jedis.lpushx(key + ":miss", "O")) ;
		// [O, C, B, A, D]
		System.out.println(jedis.lrange(key, 0, -1));

		// 同理，也可以从队尾添加
		Assert.assertEquals(Long.valueOf(6), jedis.rpushx(key, "P")) ;
		// [O, C, B, A, D, P]
		System.out.println(jedis.lrange(key, 0, -1));
		
		// 获取指定索引元素
		Assert.assertEquals("B", jedis.lindex(key, 2L)) ;
		
		// 在C前面插入M
		jedis.linsert(key, LIST_POSITION.BEFORE, "C", "M") ;
		// 在A后面插入N
		jedis.linsert(key, LIST_POSITION.AFTER, "A", "N") ;
		// [O, M, C, B, A, N, D, P]
		System.out.println(jedis.lrange(key, 0, -1));
		
		// 对队列进行裁剪，保留第2到倒数第2之间的元素
		Assert.assertEquals("OK", jedis.ltrim(key, 1, -2)) ;
		// [M, C, B, A, N, D]
		System.out.println(jedis.lrange(key, 0, -1));
		
		// 按索引设置元素(替换)
		Assert.assertEquals("OK", jedis.lset(key, 2, "T")) ;
		// [M, C, T, A, N, D]
		System.out.println(jedis.lrange(key, 0, -1));
		
		// 删除元素，删除指定数量的值为N的元素
		// 数量参数大于0时，从头部开始搜索，小于0时从尾部开始搜索，数量为参数的绝对值
		Assert.assertEquals(Long.valueOf(1), jedis.lrem(key, 2, "N")) ;
		
		// 从队列中弹出元素(取出并删除)
		// 从左边弹出
		Assert.assertEquals("M", jedis.lpop(key));
		// [C, T, A, D]
		System.out.println(jedis.lrange(key, 0, -1));
		// 从右边弹出
		Assert.assertEquals("D", jedis.rpop(key));
		// [C, T, A]
		System.out.println(jedis.lrange(key, 0, -1));
		
		// 从尾部弹出添加到新队列头部
		Assert.assertEquals("A", jedis.rpoplpush(key, newkey)) ;
		// [C, T]
		System.out.println(jedis.lrange(key, 0, -1));
		// [A]
		System.out.println(jedis.lrange(newkey, 0, -1));
		
		// 同rpoplpush，增加阻塞特性，当队列空时将阻塞
		Assert.assertEquals("T", jedis.brpoplpush(key, newkey, 1)) ;
		// [C]
		System.out.println(jedis.lrange(key, 0, -1));
		// [T, A]
		System.out.println(jedis.lrange(newkey, 0, -1));
		
		// 重新准备数据
		jedis.del(key ,newkey) ;
		jedis.lpush(key, "A" ,"B" ,"C") ;
		jedis.lpush(newkey, "A0" ,"B0" ,"C0") ;
		// [C, B, A]
		System.out.println(jedis.lrange(key, 0, -1));
		// [C0, B0, A0]
		System.out.println(jedis.lrange(newkey, 0, -1));
		
		// 阻塞式弹出，返回两列，[所属队列、值]，前面的队列优先级高，全部弹出后才会弹出后面的队列
		// [zlikun:list, C]，从左方弹出
		System.out.println(jedis.blpop(3, key ,newkey)) ;
		// [zlikun:list, A]，从右方弹出
		System.out.println(jedis.brpop(3, key ,newkey)) ;
		// [zlikun:list, B]
		System.out.println(jedis.brpop(3, key ,newkey)) ;
		// [zlikun:list:dst, A0]
		System.out.println(jedis.brpop(3, key ,newkey)) ;
		
	}
	
}
