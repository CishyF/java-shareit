package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithRequestId;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDtoResponse {

    private Integer id;
    private String description;
    private List<ItemDtoResponseWithRequestId> items;
    private LocalDateTime created;
}
