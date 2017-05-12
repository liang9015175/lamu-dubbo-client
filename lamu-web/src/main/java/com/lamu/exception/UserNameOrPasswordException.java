package com.lamu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by songliangliang on 2017/5/12.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "用户名或密码错误")
public class UserNameOrPasswordException extends RuntimeException {
}
