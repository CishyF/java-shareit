package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, EntityIsNotAvailableException.class})
    public ErrorResponse sendBadRequest(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 400 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessException.class)
    public ErrorResponse sendForbidden(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 403 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityDoesNotExistException.class, BookingByOwnerOfItemException.class})
    public ErrorResponse sendNotFound(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 404 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({EntityAlreadyExistsException.class, DataIntegrityViolationException.class})
    public ErrorResponse sendConflict(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .cause(e.getClass())
                .build();
        log.warn("Обработка исключения с кодом 409 и телом={}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse sendInternalServerError(Throwable t) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(t.getMessage())
                .cause(t.getClass())
                .build();
        log.warn("Обработка исключения с кодом 500 и телом={}", errorResponse);
        return errorResponse;
    }
}
