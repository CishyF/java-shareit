package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse sendBadRequest(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 400 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse sendUnknownState(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Unknown state: UNSUPPORTED_STATUS")
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 400 и телом={}", errorResponse);
        return errorResponse;
    }

}
