package com.lamu.controllerAdvice;

import com.lamu.exception.LamuException;
import com.lamu.util.ReplyJson;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author songliang
 * @despriction: 通用切面处理异常
 * @date: 2016/6/9
 * Created by songliang on 16/6/9.
 */
@ControllerAdvice(value = "com.lamu.controller")
public class CommonControllerAdvice {

    @ExceptionHandler(LamuException.class)
    @ResponseBody
    public ReplyJson handleException(LamuException e) {
        ReplyJson replyJson = new ReplyJson();
        String message = e.getMessage();
        ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);
        replyJson.setReplyCode(annotation.value().value());
        replyJson.setReplyInfo(message);
        return replyJson;
    }
}
