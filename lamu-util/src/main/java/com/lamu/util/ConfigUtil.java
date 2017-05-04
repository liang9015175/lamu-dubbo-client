package com.lamu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2016/1/21.
 */
public class ConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);
    public static Properties p = new Properties();

    static {
        InputStream resourceAsStream = ConfigUtil.class.getClassLoader().getResourceAsStream("lamu.properties");
        try {
            p.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("lamu.properties not found !");
        }
    }

    public static String getRedisEventSend() {
        String value = p.getProperty("redis.channel.eventSend");
        if (value == null || value.trim().length() == 0) {
            value = "eventSend";
        }
        return value;
    }
}
