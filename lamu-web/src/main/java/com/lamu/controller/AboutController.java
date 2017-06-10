package com.lamu.controller;

import com.lamu.context.RedisExecute;
import com.lamu.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by songliangliang on 2017/5/31.
 */
@RestController
@RequestMapping("/about")
public class AboutController {
    @Autowired
    private RedisExecute redisExecute;
    @Autowired
    private TestService testService;
    @RequestMapping("/us")
    public String aboutUs(final String message) {
        return testService.cache(message);

    }

    @RequestMapping(value = "/update/us")
    public String updateUs(String message) {
        return message;
    }
}
