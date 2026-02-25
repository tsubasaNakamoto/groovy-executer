package com.example.groovyexecuter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GroovyExecutionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleGroovyError(GroovyExecutionException e) {
        return Map.of("detail", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(MethodArgumentNotValidException e) {
        return Map.of("detail", "请求参数不合法，请检查 script/timeoutSeconds 字段。");
    }
}
