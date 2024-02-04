package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDtoRequest {

    @NotBlank
    private String text;
}
