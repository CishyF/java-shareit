package ru.practicum.shareit.item.util;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code ItemMapper} class converts {@code ItemDto} to {@code Item} and vice versa
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto itemToDto(Item item);

    Item dtoToItem(ItemDto dto);

    default List<ItemDto> itemsToDtos(List<Item> items) {
        return items.stream().map(this::itemToDto).collect(Collectors.toList());
    }
}
