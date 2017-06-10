package com.lamu.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Component("redisUtil")
public class RedisUtil {

    /**
     * 数据源
     */
    @Autowired
    private JedisPool jedisPool;

    /**
     * 获取数据库连接
     *
     * @return conn
     */
    public Jedis getConnection() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jedis;
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnection(Jedis jedis) {
        if (null != jedis) {
            try {
                jedisPool.returnResourceObject(jedis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
