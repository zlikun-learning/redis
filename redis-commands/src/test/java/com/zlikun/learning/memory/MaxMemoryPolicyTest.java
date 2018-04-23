package com.zlikun.learning.memory;

import com.zlikun.learning.TestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/4/23 16:03
 */
@Slf4j
public class MaxMemoryPolicyTest extends TestBase {

    @Test
    public void test() {

        // 调整redis.conf配置，设置maxmemory为1M(1024 * 1024)
        // maxmemory 1048576
        // 调整持久化(禁用)：save "" 和 appendonly no

        // 清空全部数据
        jedis.flushAll();
        jedis.flushDB();

        // 当前Redis可用内存大小
        String memory = jedis.info("Memory");
        // 输出信息见页底，通过`used_memory:868696`可知Redis启动时即会消耗一部分内存，约828KB略多一些(不固定，值为测试结论)
        log.info("memory -> \n{}", memory);
        //  提取`used_memory`值，方便后续计算：used_memory = 868696
        int maxValue = 1024 * 1024;
        long initValue = used_memory(memory);
        // 输出初始容量，剩余可用容量
        log.info("used_memory = {}, remain_memory = {}", initValue, maxValue - initValue);

        // 测试内存占用
        long prev = initValue;
        // 写入10组键值对，键长11、值长1，此时尚不能触发回收机制
        // 测试输出[40,64,128,32,192,576,1088]甚至负数等值，说明实际占用空间比写入的要大，且并不固定，但大部分是64
        // 输出负值是什么情况，还能空间占用变小了？目前猜测是由于内存回收机制造成(但测试时发现内存还没有满，我已关闭了持久化)
//        String value = "x";
        // 同样写入10组，键长仍为11，值为101
        // 测试输出[144,168,232,136,296],大部分为168
        String value = "x0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
        for (int i = 0; i < 100; i++) {
            // 键格式为："string:0001"，11字节，其值为固定1字节，总共12字节
            String key = String.format("string:%04d", i);
            jedis.set(key, value);
            // 输出每次内存占用情况，实际少输出一次，这里忽略即可
            long t = used_memory(jedis.info("Memory"));
            log.info("current_used_memory = {}, increment = {}, key_length = {}, value_length = {}, key = {}",
                    t, t - prev, key.length(), value.length(), key);
            if (maxValue - t < 1024) break; // 当容量到达只剩1KB时，不再插入，避免内存回收
            prev = t;
        }

    }

    private Pattern pattern = Pattern.compile("used_memory:(\\d+)");
    private long used_memory(String memory) {
        Matcher matcher = pattern.matcher(memory);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        } else {
            throw new RuntimeException();
        }
    }

}
/* ---------------------------------------------------------------------------------
# Memory
used_memory:868696
used_memory_human:848.34K
used_memory_rss:4235264
used_memory_rss_human:4.04M
used_memory_peak:868696
used_memory_peak_human:848.34K
used_memory_peak_perc:102.27%
used_memory_overhead:853080
used_memory_startup:786592
used_memory_dataset:15616
used_memory_dataset_perc:19.02%
total_system_memory:4124790784
total_system_memory_human:3.84G
used_memory_lua:37888
used_memory_lua_human:37.00K
maxmemory:1048576
maxmemory_human:1.00M
maxmemory_policy:volatile-lru
mem_fragmentation_ratio:4.87
mem_allocator:jemalloc-4.0.3
active_defrag_running:0
lazyfree_pending_objects:0
--------------------------------------------------------------------------------- */