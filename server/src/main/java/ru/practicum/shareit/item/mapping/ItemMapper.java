package ru.practicum.shareit.item.mapping;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.ShortBookingDtoResponse;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code ItemMapper} class converts {@code ItemDtoRequest} to {@code Item} and {@code Item} to any type of Item Request DTOs
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoResponse itemToDtoResponse(Item item);

    LongItemDtoResponse itemToLongDtoResponse(Item item);

    default LongItemDtoResponse itemToLongDtoResponse(
            Item item, ShortBookingDtoResponse lastBooking, ShortBookingDtoResponse nextBooking,
            List<CommentDtoResponse> commentDtos
    ) {
        LongItemDtoResponse dto = itemToLongDtoResponse(item);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(commentDtos);
        return dto;
    }

    Item dtoRequestToItem(ItemDtoRequest dto);

    default Item dtoRequestToItem(ItemDtoRequest dto, User owner, ItemRequest request) {
        Item item = dtoRequestToItem(dto);
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }

    default List<ItemDtoResponse> itemsToDtoResponses(List<Item> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(this::itemToDtoResponse).collect(Collectors.toList());
    }
}
