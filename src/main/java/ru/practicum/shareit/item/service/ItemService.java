package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemService {

    Item create(ItemDtoRequest itemDto, int ownerId);

    List<Item> findItemsOfUser(int ownerId);

    List<LongItemDtoResponse> findLongItemDtosOfUser(int ownerId);

    Item findById(int id);

    LongItemDtoResponse findLongItemDtoById(int id, int userId);

    List<Item> findItemsContainingText(String text);

    Item update(ItemDtoRequest dto, int id, int ownerId);

    Comment createComment(CommentDtoRequest dto, int itemId, int authorId);
}
