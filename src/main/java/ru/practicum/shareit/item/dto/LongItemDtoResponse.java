package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDtoResponse;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;

import java.util.List;

@Data
public class LongItemDtoResponse {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private ShortBookingDtoResponse lastBooking;
    private ShortBookingDtoResponse nextBooking;
    private List<CommentDtoResponse> comments;
}
