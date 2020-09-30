package cn.xxywithpq.limiter.token.bucket;

import cn.xxywithpq.limiter.conf.CustomsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Component
@Slf4j
public class TokenBucketLimiter {

    private static String tokenBucketLua;

    static {
        try {
            InputStream resourceAsStream = TokenBucketLimiter.class.getResourceAsStream("/tokenBucket.lua");
            byte[] bytes = new byte[resourceAsStream.available()];
            resourceAsStream.read(bytes);
            tokenBucketLua = new String(bytes);
        } catch (IOException e) {
            log.error("read tokenBucket.lua error {}", e);
        }
    }

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private CustomsProperties customsProperties;

    /**
     * @param businessId 限流唯一id
     * @param actionKey  限流类型
     * @param capacity   令牌桶容量
     * @param duration   令牌桶全部放满所需时间，用于计算流速(秒)
     * @return 是否允许操作
     */
    public boolean isActionAllowed(String businessId, String actionKey, long capacity, float duration) {
        try (Jedis jedis = jedisPool.getResource()) {
            // 基础信息校验
            if (StringUtils.isEmpty(businessId) || StringUtils.isEmpty(actionKey) || capacity <= 0 || duration <= 0 || capacity / duration * 0.1F <= 0) {
                return false;
            }
            String key = String.format("%s:tokenBucket:%s:%s", customsProperties.getNamespace(), actionKey, businessId);
            Object result = jedis.evalsha(jedis.scriptLoad(tokenBucketLua),
                    Arrays.asList("key", "num", "rate", "duration"),
                    Arrays.asList(key, String.valueOf(capacity), String.format("%.2f", capacity / duration), String.format("%.0f", duration)));
            return "1".equals(result.toString()) ? true : false;
        } catch (Exception e) {
            log.error("FunnelRateLimiter isActionAllowed error {}", e);
            return false;
        }
    }

}