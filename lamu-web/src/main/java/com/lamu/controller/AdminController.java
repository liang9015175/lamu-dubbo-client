package com.lamu.controller;

import com.lamu.model.AdminModel;
import com.lamu.service.AdminService;
import com.lamu.util.ReplyJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by songliang on 2016/1/5.
 *
 * @author songliang
 */
@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ReplyJson userLogin(HttpServletRequest request, HttpServletResponse response, @Validated String id, String password) {
        ReplyJson replyJson = new ReplyJson();
        if (id == null || id.equals("")) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("您没有输入用户名");
            return replyJson;
        }
        if (password == null || password.equals("")) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("您没有输入密码！");
            return replyJson;
        }
        List<AdminModel> admins = adminService.adminLogin(id, password);
        if (admins == null || admins.isEmpty()) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("用户名或者密码错误！");
            return replyJson;
        } else {
            //设置session
            request.getSession().setAttribute("admin", id);
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("登陆成功！");
            return replyJson;
        }

    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ReplyJson adminLogout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("admin");
        ReplyJson replyJson = new ReplyJson();
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo("登出成功!");
        return replyJson;
    }

}