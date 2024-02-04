package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDtoRequest {

    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private Integer itemId;
}
