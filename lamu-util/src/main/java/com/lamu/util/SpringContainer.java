package com.lamu.util;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Administrator on 2016/1/21.
 */
public class SpringContainer implements ServletContextListener {
    private static WebApplicationContext webApplicationContext;

    public static Object getBean(String name) {
        return webApplicationContext.getBean(name);
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //TODO
        //just skip
    }
}
