package com.lamu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by songliangliang on 2017/5/12.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "参数有误")
public class PreConditionException extends RuntimeException {
    public PreConditionException() {

    }

    public PreConditionException(String message) {
        super(message);
    }
}
