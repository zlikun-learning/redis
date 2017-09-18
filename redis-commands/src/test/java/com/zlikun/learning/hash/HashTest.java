package com.zlikun.learning.hash;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

/**
 * 哈希缓存测试
 * @author	zlikun
 * @date	2017年9月18日 上午9:06:05
 */
public class HashTest extends TestBase {

	protected void init() {
		// jedis.del("zlikun:hash") ;
	}

	/**
	 * 清除测试数据
	 */
	public void destroy() {
		jedis.del("zlikun:hash") ;
	}
	
	@Test
	public void hmset() {
		// 测试哈希缓存在插入时，返回状态
		Map<String ,String> hash = new HashMap<>() ;
		hash.put("name", "zlikun") ;
		hash.put("age", "120") ;
		String reply = jedis.hmset("zlikun:hash", hash) ;
		// 测试hmset命令响应值
		Assert.assertEquals("OK", reply);
	}
	
}
