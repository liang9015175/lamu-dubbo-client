package com.lamu.service;

import com.lamu.context.RedisExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

/**
 * Created by songliangliang on 2017/6/10.
 */
@Service
public class TestService {
    @Autowired
    private RedisExecute redisExecute;

    @Cacheable(value = "key_message")
    public String cache(final String message) {
        return redisExecute.execute(new RedisExecute.Callback<String>() {
            @Override
            public String exe(Jedis jedis) {
                Long keyMessage = jedis.lpush("key_message", message);
                return "success";
            }
        });
    }
}
