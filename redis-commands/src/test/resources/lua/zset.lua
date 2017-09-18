-- 传入键和当前时间戳(毫秒)，根据该值取出早于(等于)该值的元素
local key ,timestamp = KEYS[1] ,ARGV[1]
local result = redis.call('ZRANGEBYSCORE' ,key ,0 ,timestamp)

-- 将符合该条件的成员删除
redis.call('ZREMRANGEBYSCORE' ,key ,0 ,timestamp)

-- 返回结果集
return result