package ru.practicum.shareit.exception;

public class BookingByOwnerOfItemException extends RuntimeException {

    public BookingByOwnerOfItemException(String message) {
        super(message);
    }
}
