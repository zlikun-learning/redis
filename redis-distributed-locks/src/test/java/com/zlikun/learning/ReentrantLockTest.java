package com.zlikun.learning;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import java.util.concurrent.TimeUnit;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/4/23 17:24
 */
public class ReentrantLockTest {

    protected String host = "redis.zlikun.com";
    protected int port = 6379;
    private RedissonClient redisson;

    @Before
    public void init() {
        // https://github.com/redisson/redisson/wiki/2.-Configuration
        Config config = new Config();
        config.setTransportMode(TransportMode.EPOLL);
        config.useClusterServers()
                .addNodeAddress("redis://" + host + ":" + port);

        redisson = Redisson.create(config);
    }

    @After
    public void destroy() {

    }

    @Test @Ignore
    public void test() {

        // https://github.com/redisson/redisson/wiki/8.-%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%92%8C%E5%90%8C%E6%AD%A5%E5%99%A8
        RLock rLock = redisson.getLock("lock:zlikun");

        // 参考java.util.concurrent.locks.ReentrantLock用法
//        rLock.lock();
        // 指定锁于200毫秒后释放，避免死锁
        rLock.lock(200, TimeUnit.MILLISECONDS);

        // 解锁
        rLock.unlock();

    }

}
