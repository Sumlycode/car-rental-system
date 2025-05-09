package com.example.car_rental_system.exception;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class UnauthorizedException extends RuntimeException {
    private final String code;

    public UnauthorizedException(Integer code) {
        super(String.valueOf(code));
        this.code = String.valueOf(code);
    }

    public UnauthorizedException(String code) {
        super(code);
        this.code = code;
    }

    public UnauthorizedException(Integer code, String message) {
        super(message);
        this.code = String.valueOf(code);
    }

    public UnauthorizedException(Integer code, String message, Throwable ex) {
        super(message, ex);
        this.code = String.valueOf(code);
    }

}