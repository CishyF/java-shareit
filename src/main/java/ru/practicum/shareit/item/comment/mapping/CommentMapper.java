package ru.practicum.shareit.item.comment.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "created", source = "comment.createdAt")
    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDtoResponse commentToDtoResponse(Comment comment);

    Comment dtoRequestToComment(CommentDtoRequest dto);

    default Comment dtoRequestToComment(CommentDtoRequest dto, Item item, User author) {
        Comment comment = dtoRequestToComment(dto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    default List<CommentDtoResponse> commentsToDtoResponses(List<Comment> comments) {
        return comments.stream().map(this::commentToDtoResponse).collect(Collectors.toList());
    }
}
