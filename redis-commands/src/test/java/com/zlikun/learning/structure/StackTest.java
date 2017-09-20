package com.zlikun.learning.structure;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

import redis.clients.jedis.Jedis;

/**
 * 使用List结构模拟栈结构(先进后出)，这里仅作演示用，不用在意jedis对象作为类属性无法回收的问题
 * @author	zlikun
 * @date	2017年9月20日 下午3:56:11
 */
public class StackTest extends TestBase {

	private static class Stack {
		
		private Jedis jedis ;
		private String stackName ;
		
		public Stack(Jedis jedis ,String stackName) {
			this.jedis = jedis ;
			this.stackName = stackName ;
		}
		
		public void push(String element) {
			this.jedis.lpush(stackName, element) ;
		}
		
		public String pop() {
			return this.jedis.lpop(stackName) ;
		}
		
		public boolean isEmpty() {
			return !jedis.exists(stackName) ;
		}
		
	}
	
	@Test
	public void test() {
		
		Stack stack = new Stack(jedis, "stack") ;
		
		stack.push("A");
		stack.push("B");
		stack.push("C");
		
		Assert.assertEquals("C", stack.pop());
		Assert.assertEquals("B", stack.pop());
		Assert.assertEquals("A", stack.pop());
		
		Assert.assertTrue(stack.isEmpty());
	}
	
	@Override
	protected void destroy() {
		jedis.del("stack") ;
	}
	
}
