package com.lamu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by songliangliang on 2017/5/12.
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "推荐大于指定数量")
public class RecommandGtFourException extends RuntimeException {
}
