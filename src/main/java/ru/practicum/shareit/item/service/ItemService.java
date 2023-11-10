package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(ItemDtoRequest itemDto, int ownerId);

    List<Item> findAllItemsOfUser(int ownerId);

    Item findById(int id);

    List<Item> findItemsContainingText(String text);

    Item update(int ownerId, int id, ItemDtoRequest dto);
}
