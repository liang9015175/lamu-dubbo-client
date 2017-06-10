package com.lamu.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by songliangliang on 2017/6/9.
 */
@Component("redisExecute")
public class RedisExecute {

    private static final Logger log = LoggerFactory.getLogger(RedisExecute.class);
    @Autowired
    private JedisPool jedisPool;

    public <T> T execute(Callback<T> callback) {
        T t = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null || callback == null) {
            return t;
        }
        try {
            t = callback.exe(jedis);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return t;
    }

    public interface Callback<T> {
        T exe(Jedis jedis);
    }
}
