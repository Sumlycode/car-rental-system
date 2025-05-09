package com.example.car_rental_system.exception;

import com.example.car_rental_system.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.StringJoiner;

import static com.example.car_rental_system.constant.StatusCodeConstant.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ResponseModel<Void>> handleGenericException(Exception ex) {
        if (ex instanceof ResourceAccessException || (ex instanceof HttpServerErrorException && HttpStatus.GATEWAY_TIMEOUT.equals(((HttpServerErrorException) ex).getStatusCode()))) {
            log.error("error : {}", ex);
            return new ResponseEntity<>(new ResponseModel<>(RESPONSE_STATUS_504)
                    , HttpStatus.GATEWAY_TIMEOUT);
        }
        log.error("error : {}", ex);
        return new ResponseEntity<>(
                new ResponseModel<>(RESPONSE_STATUS_500, "Internal Server Error", ex.getMessage())
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ResponseModel<?>> handleBusinessException(MethodArgumentNotValidException ex) {
        StringJoiner fruitJoiner = new StringJoiner(", ");
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            fruitJoiner.add(error.getDefaultMessage());
        }
        log.error("error : {}", ex);
        return new ResponseEntity<>(
                new ResponseModel<>(RESPONSE_STATUS_400, "Bad Request", fruitJoiner.toString())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                new ResponseModel<>(RESPONSE_STATUS_401, ex.getMessage())
                , HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ResponseModel<>(RESPONSE_STATUS_404, ex.getMessage())
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleUnauthorizedException(BusinessException ex, WebRequest request) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                new ResponseModel<>(ex.getCode(), ex.getMessage())
                , HttpStatus.OK);

    }

    @ExceptionHandler(DuplicateException.class)
    protected ResponseEntity<Object> handleDuplicateException(DuplicateException ex) {
        log.error(ex.getMessage());
        String message;
        String description = "";
        if (StringUtils.hasLength(ex.getMessage()) && ex.getMessage().contains("||")) {
            message = ex.getMessage().split("\\|\\|")[0];
            description = ex.getMessage().split("\\|\\|")[1];
        } else {
            message = ex.getMessage();
        }
        return new ResponseEntity<>(
                new ResponseModel<>(RESPONSE_STATUS_409, message, description)
                , HttpStatus.CONFLICT);

    }

    @ExceptionHandler(UnprocessableException.class)
    protected ResponseEntity<Object> handleDuplicateException(UnprocessableException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                new ResponseModel<>(RESPONSE_STATUS_422, ex.getMessage())
                , HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<Object> handleDuplicateException(ForbiddenException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                new ResponseModel<>(RESPONSE_STATUS_403, ex.getMessage())
                , HttpStatus.FORBIDDEN);
    }
}