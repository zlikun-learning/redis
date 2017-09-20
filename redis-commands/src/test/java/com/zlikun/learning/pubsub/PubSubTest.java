package com.zlikun.learning.pubsub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.zlikun.learning.TestBase;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * 测试发布、订阅模式命令
 * @author	zlikun
 * @date	2017年9月20日 下午6:20:37
 */
public class PubSubTest extends TestBase {

	@Test
	public void test() {
		
		final String channel0 = "c0" ;
		final String channel2 = "c2" ;
		
		// 启动三个订阅端
		ExecutorService exec = Executors.newFixedThreadPool(3) ;
		for (int i = 0 ; i < 3  ;i ++) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					Jedis jedis = new Jedis(host, port) ;
					// 按通道订阅
					jedis.subscribe(new Subscriber(), channel0, channel2);
					// 按模式订阅
					// jedis.psubscribe(new Subscriber(), "c*");
					jedis.close();
				}
			});
		}
		
		// 发布消息
		for (int i = 0 ; i < 4 ;i ++) {
			jedis.publish(channel0, "0-Hello - " + i) ;
			jedis.publish(channel2, "2-Hello - " + i) ;
		}
		
		// 统计指定模式的channel
		// [c2, c0]
		System.err.println(jedis.pubsubChannels("c*")) ;
		
	}
	
	/**
	 * 订阅者
	 * @author	zlikun
	 * @date	2017年9月20日 下午6:36:07
	 */
	private static class Subscriber extends JedisPubSub {
		
		/**
		 * 处理订阅到的消息，重复执行，每接收到一消息即执行一次
		 */
		@Override
		public void onMessage(String channel, String message) {
			System.out.println(String.format("channel = %s ,message = %s", channel ,message));
		}
		
		/**
		 * 初始化订阅事件监听，执行一次
		 */
		@Override
		public void onSubscribe(String channel, int subscribedChannels) {
			System.err.println(String.format("初始化订阅监听：channel = %s ,subscribedChannels = %d"
					, channel ,subscribedChannels));
		}
		
		/**
		 * 取消订阅事件监听，执行一次
		 */
		@Override
		public void onUnsubscribe(String channel, int subscribedChannels) {
			System.err.println(String.format("取消订阅监听：channel = %s ,subscribedChannels = %d"
					, channel ,subscribedChannels));
		}

		/**
		 * @see #onMessage(String, String)
		 * 针对pattern模式事件监听
		 */
		@Override
		public void onPMessage(String pattern, String channel, String message) {

		}
		
		/**
		 * @see #onSubscribe(String, int)
		 * 针对pattern模式事件监听
		 */
		@Override
		public void onPSubscribe(String pattern, int subscribedChannels) {

		}
		
		/**
		 * @see #onUnsubscribe(String, int)
		 * 针对pattern模式事件监听
		 */
		@Override
		public void onPUnsubscribe(String pattern, int subscribedChannels) {

		}
		
	}
	
}
