package ru.practicum.shareit.item.util;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;

/**
 * The {@code ItemPatchUpdater} class updates field(-s) of got {@code Item} instance
 * according to {@code ItemDto} field(-s)
 */
@Mapper(componentModel = "spring")
public interface ItemPatchUpdater {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItem(@MappingTarget Item item, ItemDto dto);
}
