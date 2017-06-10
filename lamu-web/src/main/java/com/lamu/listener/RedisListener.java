package com.lamu.listener;

import com.lamu.context.RedisUtil;
import com.lamu.util.ConfigUtil;
import com.lamu.util.SpringContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by songliangliang on 2017/6/9.
 */
public class RedisListener implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(RedisListener.class);
    private ExecutorService executorService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.info("redis 消息发布订阅机制开始启动......");
        final String channel = ConfigUtil.getRedisEventSend() + "_*";
        final RedisUtil redisUtil = (RedisUtil) SpringContainer.getBean("redisUtil");
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                MyListener listener = new MyListener();
                Jedis jedis = redisUtil.getConnection();
                jedis.psubscribe(listener, new String[]{channel});
            }
        });
        LOG.info("redis 消息发布订阅机制启动完毕......");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
