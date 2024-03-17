package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoRequest {

    private String description;
}
