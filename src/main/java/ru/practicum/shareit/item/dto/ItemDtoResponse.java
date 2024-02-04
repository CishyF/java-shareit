package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDtoResponse {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
}
