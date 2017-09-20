# Redis

本案使用`jedis`操作`redis`

#### 参考资料
- [服务器信息](http://www.redis.net.cn/tutorial/3518.html)

#### 数据结构

- 常用数据结构：String、List、Hash、Set、ZSet

- HyperLogLog
> 什么是基数？  
> 比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素)为5。  
> 基数估计就是在误差可接受的范围内，快速计算基数。  
> 每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基 数。HyperLogLog 是用来做基数统计的算法的。

#### [数据备份与恢复](http://www.redis.net.cn/tutorial/3519.html)
```
# 手动创建当前数据库备份
> SAVE
OK

# 该命令将在 redis 安装目录中创建dump.rdb文件

# 后台执行数据备份
> BGSAVE
Background saving started

# 恢复数据只需要将备份的dump.rdb文件移动到安装目录并重启服务即可
# 查看 Redis 安装目录
> CONFIG GET dir
1) "dir"
2) "/data"
```