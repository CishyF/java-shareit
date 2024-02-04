package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

@Data
public class BookingDtoResponse {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private ItemDtoResponse item;
    private UserDtoResponse booker;
}
