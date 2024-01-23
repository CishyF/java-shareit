package ru.practicum.shareit.item.comment.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ItemRepository itemRepository;

    @Mapping(target = "created", source = "comment.createdAt")
    @Mapping(target = "authorName", source = "comment.author.name")
    public abstract CommentDtoResponse commentToDtoResponse(Comment comment);

    protected abstract Comment dtoRequestToComment(CommentDtoRequest dto);

    public Comment dtoRequestToComment(CommentDtoRequest dto, int itemId, int authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка использования несуществующего автора при создании комментария"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка оставления комментария у несуществующей вещи"));
        Comment comment = dtoRequestToComment(dto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    public List<CommentDtoResponse> commentsToDtoResponses(List<Comment> comments) {
        return comments.stream().map(this::commentToDtoResponse).collect(Collectors.toList());
    }
}
