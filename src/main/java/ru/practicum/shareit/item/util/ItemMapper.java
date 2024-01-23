package ru.practicum.shareit.item.util;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.ShortBookingDtoResponse;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.util.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code ItemMapper} class converts {@code ItemDtoRequest} to {@code Item} and {@code Item} to any type of Item Request DTOs
 */
@Mapper(componentModel = "spring")
public abstract class ItemMapper {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected CommentMapper commentMapper;

    public abstract ItemDtoResponse itemToDtoResponse(Item item);

    protected abstract LongItemDtoResponse itemToLongDtoResponse(Item item);

    public LongItemDtoResponse itemToLongDtoResponse(
            Item item, ShortBookingDtoResponse lastBooking, ShortBookingDtoResponse nextBooking
    ) {
        LongItemDtoResponse dto = itemToLongDtoResponse(item);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        List<Comment> comments = commentRepository.findAllByItem(item);
        List<CommentDtoResponse> commentDtos = commentMapper.commentsToDtoResponses(comments);
        dto.setComments(commentDtos);
        return dto;
    }

    protected abstract Item dtoRequestToItem(ItemDtoRequest dto);

    public Item dtoRequestToItem(ItemDtoRequest dto, int ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка использования вещи несуществующего пользователя"));
        Item item = dtoRequestToItem(dto);
        item.setOwner(owner);
        return item;
    }

    public List<ItemDtoResponse> itemsToDtoResponses(List<Item> items) {
        return items.stream().map(this::itemToDtoResponse).collect(Collectors.toList());
    }
}
