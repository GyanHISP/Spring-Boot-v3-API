package com.wowApp.exception;

import com.wowApp.enums.ResponseEnum;

public class CustomException extends  RuntimeException{
    private Integer code;
    public CustomException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.code = responseEnum.getCode();
    }
    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
