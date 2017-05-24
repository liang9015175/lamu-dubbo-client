package com.lamu.controller;

import com.alibaba.fastjson.JSON;
import com.lamu.exception.NotFoundException;
import com.lamu.model.UserModel;
import com.lamu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by songliangliang on 2017/5/24.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/login")
    public void login(String username, String password) {
        UserModel login = userService.login(username, password);
        if (login == null) {
            throw new NotFoundException("用户名或密码错误");
        }
        request.getSession().setAttribute("user", JSON.toJSONString(login));
        Cookie cookie = new Cookie("user", JSON.toJSONString(login));
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
        return;
    }

}
