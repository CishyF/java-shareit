package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.entity.BookingStatus;

import java.time.LocalDateTime;

@Data
public class ShortBookingDtoResponse {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Integer bookerId;
}
