redis.replicate_commands()
local nowStr = redis.call('TIME')[1]
local bulk = redis.call('EXISTS', ARGV[1])
if (bulk == 0) then
    redis.call('HMSET', ARGV[1], 'lastTimeStamp', nowStr, 'num', ARGV[2])
    redis.call('EXPIRE', ARGV[1], math.floor(ARGV[4]) + 10)
end
local table = redis.call('HGETALL', ARGV[1])
local plusNum = (nowStr - table[2]) * ARGV[3]
if (plusNum ~= 0) then
    redis.call('HMSET', ARGV[1], 'lastTimeStamp', nowStr, 'num', plusNum + table[4])
    redis.call('EXPIRE', ARGV[1], math.floor(ARGV[4]) + 10)
end
local newNum = redis.call('HGET', ARGV[1], 'num')
if (math.floor(newNum) > 0) then
    redis.call('HSET', ARGV[1], 'num', newNum - 1)
    return 1 else return 0
end