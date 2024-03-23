package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
