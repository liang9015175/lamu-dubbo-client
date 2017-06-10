package com.lamu.context;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by songliangliang on 2017/6/9.
 */

@Configuration
@EnableCaching()
public class RedisBeanContext extends CachingConfigurerSupport {
    @Value("${redis.database}")
    private Integer database;
    @Value("${redis.cache.database}")
    private Integer cacheDatabase;
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.pass}")
    private String password;
    @Value("${poll.minIdle}")
    private int minIdle;
    @Value("${poll.maxIdle}")
    private int maxIdle;
    @Value("${poll.maxTotal}")
    private int maxTotal;
    @Value("${poll.maxWaitMillis}")
    private int maxWaitMillis;
    @Value("${poll.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${redis.timeout}")
    private Integer timeout;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactoryCache() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        factory.setPoolConfig(poolConfig);
        factory.setDatabase(cacheDatabase);
        factory.setHostName(host);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setTimeout(5000);
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateCache(JedisConnectionFactory jedisConnectionFactoryCache) {
        StringRedisTemplate template = new StringRedisTemplate(jedisConnectionFactoryCache);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(om);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager redisCacheManager(RedisTemplate redisTemplateCache) {
        RedisCacheManager manager = new RedisCacheManager(redisTemplateCache);
        manager.setDefaultExpiration(timeout);
        return manager;
    }

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, 5000, password, database);
        return jedisPool;
    }

}
