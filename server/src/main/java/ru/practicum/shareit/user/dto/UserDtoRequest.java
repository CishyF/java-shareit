package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoRequest {

    private String name;
    private String email;
}
