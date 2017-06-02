package com.lamu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by songliangliang on 2017/5/31.
 */
@RestController
@RequestMapping("/about")
public class AboutController {
    @RequestMapping("/us")
    public String aboutUs() {
        return "";
    }
}
