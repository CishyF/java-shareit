package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {

    ItemDtoResponse create(ItemDtoRequest itemDto, int ownerId);

    List<ItemDtoResponse> findAllItemsOfUser(int ownerId);

    ItemDtoResponse findById(int id);

    List<ItemDtoResponse> findItemsContainingText(String text);

    ItemDtoResponse update(int ownerId, int id, ItemDtoRequest dto);
}
