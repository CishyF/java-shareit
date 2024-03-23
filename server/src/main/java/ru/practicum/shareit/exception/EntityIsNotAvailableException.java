package ru.practicum.shareit.exception;

public class EntityIsNotAvailableException extends RuntimeException {

    public EntityIsNotAvailableException(String message) {
        super(message);
    }
}
