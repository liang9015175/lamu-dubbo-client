package com.lamu.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by Administrator on 2016/1/21.
 */
public class MyListener extends JedisPubSub {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyListener.class);
    /**
     * 取得订阅的消息的处理
     *
     * @param channel
     * @param message
     */
    public void onMessage(String channel, String message) {
        LOGGER.info("Mylistener.onMessage()");
        LOGGER.info(channel + "=" + message);
    }

    /**
     * 初始化订阅的时候处理
     *
     * @param channel
     * @param subscribedChannels
     */
    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.info("MyListener.onSubscribe()");
        LOGGER.info(channel + "=" + subscribedChannels);
    }

    /**
     * 取消订阅
     *
     * @param channel
     * @param subscribedChannels
     */
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOGGER.info("MyListener.onUnsubscribe()");
        LOGGER.info(channel + "=" + subscribedChannels);

    }

    /**
     * 按照表达式初始化订阅的处理方式
     *
     * @param pattern
     * @param subscribedChannels
     */
    public void onPSubscribe(String pattern, int subscribedChannels) {
        LOGGER.info("MyListener.onPSubscribe()");
        LOGGER.info(pattern + "=" + subscribedChannels);

    }

    /**
     * 按表达式取消订阅的处理方式
     *
     * @param patten
     * @param subscribedChannels
     */
    public void onPUnsubscribe(String patten, int subscribedChannels) {
        LOGGER.info("MyListener.onPUnsubscribe()");
        LOGGER.info(patten + "=" + subscribedChannels);

    }

    public void onPMessage(String patten, String channel, String message) {
        //TODO
    }

}
