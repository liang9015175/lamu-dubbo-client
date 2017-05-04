package com.lamu.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/1/21.
 */
@Controller(value = "order")
public class OrderController {
    @ResponseBody
    @RequestMapping(value = "post", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String submitOrder(HttpServletRequest request, HttpServletResponse response, String orderData) {
        return "aaa";
    }
}
