package com.zlikun.learning;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试Jedis实现一致哈希
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017-11-15 11:43
 */
public class ShardedJedisTest {

    ShardedJedisPool shardedJedisPool ;

    @Before
    public void init() {
        // 连接池配置
        JedisPoolConfig config = new JedisPoolConfig() ;

        // 分片节点配置
        List<JedisShardInfo> shards = new ArrayList<>() ;
        shards.add(new JedisShardInfo("192.168.9.205" ,6379)) ;
        shards.add(new JedisShardInfo("192.168.9.170" ,19000)) ;

        // 指定哈希算法，默认：MURMUR_HASH
        shardedJedisPool = new ShardedJedisPool(config ,shards , Hashing.MURMUR_HASH) ;
    }

    @Test
    public void test() {

        for (int i = 0; i < 100; i++) {
            ShardedJedis jedis = null ;
            try {
                jedis = shardedJedisPool.getResource() ;
                jedis.hset("zlikun:hash:" + i ,"number" ,String.valueOf(i)) ;
            } finally {
                jedis.close();
            }
        }

    }

    @After
    public void destroy() {
        shardedJedisPool.close();
    }

}
