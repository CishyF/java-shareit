package ru.practicum.shareit.item.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}
