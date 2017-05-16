package com.lamu.controller;

import java.io.Serializable;

public class ExceptionRepresent implements Serializable {
    String message;
    Integer httpStatus;

    ExceptionRepresent() {

    }

    ExceptionRepresent(Integer httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }
}