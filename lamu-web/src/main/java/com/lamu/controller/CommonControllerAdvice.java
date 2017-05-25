package com.lamu.controller;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@ControllerAdvice
public class CommonControllerAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ExceptionRepresent> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "系统繁忙";
        ResponseStatus responseStatus = (ResponseStatus) AnnotatedElementUtils.findMergedAnnotation(e.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
            message = e.getLocalizedMessage();
        }
        ExceptionRepresent represent = new ExceptionRepresent(status.value(), message);
        return new ResponseEntity<ExceptionRepresent>(represent, status);
    }

}
