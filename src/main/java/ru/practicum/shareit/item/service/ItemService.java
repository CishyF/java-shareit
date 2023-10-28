package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, int ownerId);

    List<ItemDto> findAllItemsOfUser(int ownerId);

    ItemDto findById(int id);

    List<ItemDto> findItemsContainingText(String text);

    ItemDto update(int ownerId, int id, ItemDto dto);
}
