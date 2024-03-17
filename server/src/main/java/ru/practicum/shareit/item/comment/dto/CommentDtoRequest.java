package ru.practicum.shareit.item.comment.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoRequest {

    private String text;
}
