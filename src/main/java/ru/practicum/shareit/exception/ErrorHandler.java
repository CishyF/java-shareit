package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class, IllegalArgumentException.class})
    public ErrorResponse sendBadRequest(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 400 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessException.class)
    public ErrorResponse sendForbidden(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 403 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityDoesNotExistException.class)
    public ErrorResponse sendNotFound(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 404 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ErrorResponse sendConflict(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 409 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse sendInternalServerError(Throwable t) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(t.getMessage())
                .cause(t.getClass())
                .build();
        log.warn("Обработка исключения с кодом 500 и телом={}", errorResponse);
        return errorResponse;
    }
}
