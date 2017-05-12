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

    public static String getMchId() {
        String mchId = p.getProperty("mch_id");
        if (mchId == null || mchId.trim().length() == 0) {
            throw new RuntimeException("mch_id not found,plz check your config!");
        }
        return mchId;
    }

    public static String getNoticeUrl() {
        String value = p.getProperty("notify_url");
        if (value == null || value.trim().length() == 0) {
            throw new RuntimeException("notice url not found,plz check your config!");
        }
        return value;
    }

    public static String getReqUrl() {
        String value = p.getProperty("req_url");
        if (value == null || value.trim().length() == 0) {
            throw new RuntimeException("req url not found,plz check your config!");
        }
        return value;
    }

    public static String getKey() {
        String value = p.getProperty("key");
        if (value == null || value.trim().length() == 0) {
            throw new RuntimeException("key  not found,plz check your config!");
        }
        return value;
    }

    public static String getService() {
        String value = p.getProperty("service");
        if (value == null || value.trim().length() == 0) {
            throw new RuntimeException("key  not found,plz check your config!");
        }
        return value;
    }
}
