package com.example.car_rental_system.exception;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class DuplicateException extends RuntimeException {
    private final int code;

    public DuplicateException(Integer code) {
        this.code = code;
    }

    public DuplicateException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public DuplicateException(Integer code, String message, Throwable ex) {
        super(message, ex);
        this.code = code;
    }

}