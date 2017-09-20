package com.zlikun.learning.transaction;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

import redis.clients.jedis.Transaction;

/**
 * Redis事务
 * @author	zlikun
 * @date	2017年9月20日 下午6:56:23
 */
public class TransactionTest extends TestBase {

	final String key = "key" ;
	
	@Override
	protected void init() {
		jedis.del(key) ;
	}
	
	@Test
	public void multi() {
		
		// 以MULTI开启一个事务
		Transaction transaction = jedis.multi() ;

		// 执行写入命令
		transaction.set(key, "ABC") ;
		
		// DISCARD 取消事务，事务执行块内的所有命令
		Assert.assertEquals("OK" ,transaction.discard()) ;
		
		// 事务取消，所以数据未写入
		Assert.assertFalse(jedis.exists(key));
		
		
		// 开启事务
		transaction = jedis.multi() ;
		
		// OK
		transaction.set(key, "zlikun") ;
		
		// 1
		transaction.expire(key, 30) ;
		
		// 22
		transaction.append(key, " is a good boy !") ;
		
		// zlikun is a good boy !
		transaction.get(key) ;
		
		// 最后由EXEC触发事务，返回事务内全部操作的返回值
		List<Object> list = transaction.exec() ;
		
		/*
		 * OK
		 * 1
		 * 22
		 * zlikun is a good boy !
		 */
		list.stream().forEach(System.out::println);

		Assert.assertEquals("zlikun is a good boy !", jedis.get(key));
		
	}
	
	@Test
	public void watch() {
		
		// 在key上设置监视，当该key在事务执行前被其它命令改动，事务将被打断
		Assert.assertEquals("OK", jedis.watch(key)) ;
		
		// jedis.set(key ,"hello") ;	// 事务前key被改动
		
		Transaction transaction = jedis.multi() ;
		
		transaction.set(key ,"zlikun") ;
		
		transaction.exec() ;
		
		// 取消所有Key监听
		Assert.assertEquals("OK", jedis.unwatch()) ;
		
//		// 事务未生效，值为改动前的值
//		Assert.assertEquals("hello", jedis.get(key));
		
		// 事务生效，值 为事务中修改的值
		Assert.assertEquals("zlikun", jedis.get(key));
		
		
	}
	
}
