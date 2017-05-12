package com.lamu.controller;

import com.lamu.exception.UserNameOrPasswordException;
import com.lamu.model.AdminModel;
import com.lamu.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void userLogin(String id, String password) {
        List<AdminModel> models = adminService.adminLogin(id, password);
        if (models == null || models.isEmpty()) {
            throw new UserNameOrPasswordException();
        }

    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public void adminLogout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("admin");
    }

}