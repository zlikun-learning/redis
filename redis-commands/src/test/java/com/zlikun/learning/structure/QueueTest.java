package com.zlikun.learning.structure;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

import redis.clients.jedis.Jedis;

/**
 * 模拟队列结构(先进先出)
 * @author	zlikun
 * @date	2017年9月20日 下午4:03:11
 */
public class QueueTest extends TestBase {

	private static class Queue {
		
		private Jedis jedis ;
		private String queueName ;
		
		public Queue(Jedis jedis ,String queueName) {
			this.jedis = jedis ;
			this.queueName = queueName ;
		}
		
		public void push(String element) {
			this.jedis.lpush(queueName, element) ;
		}
		
		public String take() {
			return this.jedis.rpop(queueName) ;
		}
		
		public boolean isEmpty() {
			return !jedis.exists(queueName) ;
		}
		
	}
	
	@Test
	public void test() {
		
		Queue stack = new Queue(jedis, "queue") ;
		
		stack.push("A");
		stack.push("B");
		stack.push("C");
		
		Assert.assertEquals("A", stack.take());
		Assert.assertEquals("B", stack.take());
		Assert.assertEquals("C", stack.take());
		
		Assert.assertTrue(stack.isEmpty());
	}
	
	@Override
	protected void destroy() {
		jedis.del("queue") ;
	}
	
}
