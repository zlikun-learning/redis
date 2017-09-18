package com.zlikun.learning.commands;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.zlikun.learning.TestBase;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

/**
 * 有序集合
 * @author	zlikun
 * @date	2017年9月18日 上午10:23:59
 */
@Slf4j
public class ZSetTest extends TestBase {

	private String key = "zlikun:zset" ;
	
	@Override
	protected void init() {
		jedis.del(key) ;
	}
	
	@Override
	protected void destroy() {
		jedis.del(key) ;
	}
	
	@Test
	public void commands() {
		
		// 插入元素($key ,$score ,$member)
		// 注意，插入命令返回一个整型返回值，1表示插入、0表示更新(覆盖已有值/分数)
		Assert.assertEquals(Long.valueOf(1), jedis.zadd(key, 98.0, "zlikun")) ;
		Assert.assertEquals(Long.valueOf(1), jedis.zadd(key, 67.5, "nginx")) ;
		Assert.assertEquals(Long.valueOf(1), jedis.zadd(key, 62.0, "apache")) ;
		Assert.assertEquals(Long.valueOf(1), jedis.zadd(key, 83.0, "jenkins")) ;
		Assert.assertEquals(Long.valueOf(0), jedis.zadd(key, 87.0, "jenkins")) ;
		Assert.assertEquals(Long.valueOf(1), jedis.zadd(key, 100.0, "tomcat")) ;
		
		// 查询指定成员分数值
		Assert.assertEquals(Double.valueOf(87.0), jedis.zscore(key, "jenkins"));
		
		// 查询元素索引值 
		Assert.assertEquals(Long.valueOf(1), jedis.zrank(key, "nginx"));
		// 查询元素倒序索引值
		Assert.assertEquals(Long.valueOf(3), jedis.zrevrank(key, "nginx"));
		
		
		// 查询元素个数
		Assert.assertEquals(Long.valueOf(5), jedis.zcard(key));
		
		// 计算指定分数区间元素个数($key ,$minScore ,$maxScore)
		// 注意：分数区间包含边界值，如本例中的100.0
		Assert.assertEquals(Long.valueOf(3), jedis.zcount(key, 80, 100));
		
		// 计算指定字典区间元素个数(个人不是太理解这块!)
		Assert.assertEquals(Long.valueOf(5), jedis.zlexcount(key, "-", "+"));
		Assert.assertEquals(Long.valueOf(2), jedis.zlexcount(key, "[j", "[z"));
		
		// 元素分数自增(负数表示自减)
		Double score = jedis.zincrby(key, 1.5, "apache") ;
		Assert.assertNotNull(score);
		Assert.assertEquals(Double.valueOf(63.5), score);
		Assert.assertEquals(Double.valueOf(63.5), jedis.zscore(key, "apache"));
		
		// 按索引查询，0/-1表示查询全部
		// [apache, nginx, jenkins, zlikun, tomcat] 默认按分数排序
		log.info("{}" ,jedis.zrange(key, 0, -1));
		// 按分数区间查询，包含边界值
		// [jenkins, zlikun, tomcat] 
		log.info("{}" ,jedis.zrangeByScore(key, 80.0, 100.0));
		// 按字典区间查询
		// [nginx, jenkins]
		log.info("{}" ,jedis.zrangeByLex(key, "[j", "[z"));
		
		// 上面的查询方法都可以使用`WithScores`来同时获取分数，对应命令行中的`WITHSCORES`参数
		Set<Tuple> result = jedis.zrangeWithScores(key, 0, -1) ;
		// [apache / 63.5] ,[nginx / 67.5] ,[jenkins / 87.0] ,[zlikun / 98.0] ,[tomcat / 100.0]
		for(Tuple tuple : result) {
			log.info("[{} / {}]" ,tuple.getElement() ,tuple.getScore());
		}
		
		// 按倒序排列查询
		// [tomcat, zlikun, jenkins, nginx, apache]
		log.info("{}" ,jedis.zrevrange(key, 0, -1)) ;

		// 迭代有序集合中的元素(该命令待后续研究)
		ScanResult<Tuple> scanResult = jedis.zscan(key, "80") ;
		for(Tuple tuple : scanResult.getResult()) {
			log.info("[{} / {}]" ,tuple.getElement() ,tuple.getScore());
		}
		
		// 删除元素(指定成员)
		Assert.assertEquals(Long.valueOf(2), jedis.zrem(key, "nginx" ,"tomcat")) ;
		// 删除元素(指定分数区间)
		Assert.assertEquals(Long.valueOf(1), jedis.zremrangeByScore(key, 60, 80)) ;
		// 删除元素(指定索引区间)
		Assert.assertEquals(Long.valueOf(2), jedis.zremrangeByRank(key, 0, 1)) ;
		// 删除元素(指定词典区间)
		Assert.assertEquals(Long.valueOf(0), jedis.zremrangeByLex(key, "-", "+")) ;
		
	}
	
}
