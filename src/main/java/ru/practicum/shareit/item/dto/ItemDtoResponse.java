package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoResponse {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
