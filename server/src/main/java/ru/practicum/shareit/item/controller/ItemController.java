package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.mapping.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.mapping.ItemMapper;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @GetMapping
    public Collection<LongItemDtoResponse> getItemsOfUser(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @RequestParam int from,
            @RequestParam int size
    ) {
        return itemService.findLongItemDtosOfUser(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public LongItemDtoResponse getItemById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int id
    ) {
        return itemService.findLongItemDtoById(id, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDtoResponse> getItemsContainingText(
            @RequestParam String text,
            @RequestParam int from,
            @RequestParam int size
    ) {
        return itemMapper.itemsToDtoResponses(
                itemService.findItemsContainingText(text, from, size)
        );
    }

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestBody @Valid ItemDtoRequest dto) {
        return itemMapper.itemToDtoResponse(itemService.create(dto, ownerId));
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse patchItem(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @PathVariable int id,
            @RequestBody ItemDtoRequest dto
    ) {
        return itemMapper.itemToDtoResponse(itemService.update(dto, id, ownerId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(
            @RequestHeader("X-Sharer-User-Id") int authorId,
            @PathVariable int itemId,
            @RequestBody CommentDtoRequest dto
    ) {
        return commentMapper.commentToDtoResponse(itemService.createComment(dto, itemId, authorId));
    }
}
