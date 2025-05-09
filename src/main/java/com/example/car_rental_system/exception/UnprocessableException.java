package com.example.car_rental_system.exception;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class UnprocessableException extends RuntimeException {
    private final int code;

    public UnprocessableException(Integer code) {
        this.code = code;
    }

    public UnprocessableException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public UnprocessableException(Integer code, String message, Throwable ex) {
        super(message, ex);
        this.code = code;
    }

}