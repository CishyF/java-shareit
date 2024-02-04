package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDtoResponse {

    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
