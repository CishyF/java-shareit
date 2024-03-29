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
        log.info("Пришел GET-запрос /items?from={}&size={}, userId={} без тела", from, size, ownerId);
        Collection<LongItemDtoResponse> itemsDto = itemService.findLongItemDtosOfUser(ownerId, from, size);
        log.info("Ответ на GET-запрос /items?from={}&size={}, userId={} с телом={}", from, size, ownerId, itemsDto);
        return itemsDto;
    }

    @GetMapping("/{itemId}")
    public LongItemDtoResponse getItemById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int itemId
    ) {
        log.info("Пришел GET-запрос /items/{id={}}, userId={} без тела", itemId, userId);
        LongItemDtoResponse itemDto = itemService.findLongItemDtoById(itemId, userId);
        log.info("Ответ на GET-запрос /items/{id={}}, userId={} с телом={}", itemId, userId, itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public Collection<ItemDtoResponse> getItemsContainingText(
            @RequestParam String text,
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("Пришел GET-запрос /items/search?text={}&from={}&size={} без тела", text, from, size);
        Collection<ItemDtoResponse> itemDtos = itemMapper.itemsToDtoResponses(
                itemService.findItemsContainingText(text, from, size)
        );
        log.info("Ответ на GET-запрос /items/search?text={}&from={}&size={} с телом={}", text, from, size, itemDtos);
        return itemDtos;
    }

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestBody @Valid ItemDtoRequest dto) {
        log.info("Пришел POST-запрос /items, userId={} с телом={}", ownerId, dto);
        ItemDtoResponse savedDto = itemMapper.itemToDtoResponse(itemService.create(dto, ownerId));
        log.info("Ответ на POST-запрос /items, userId={} с телом={}", ownerId, savedDto);
        return savedDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse patchItem(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @PathVariable int itemId,
            @RequestBody ItemDtoRequest dto
    ) {
        log.info("Пришел PATCH-запрос /items/{id={}}, userId={} с телом={}", itemId, ownerId, dto);
        ItemDtoResponse updatedDto = itemMapper.itemToDtoResponse(itemService.update(dto, itemId, ownerId));
        log.info("Ответ на PATCH-запрос /items/{id={}}, userId={} с телом={}", itemId, ownerId, updatedDto);
        return updatedDto;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(
            @RequestHeader("X-Sharer-User-Id") int authorId,
            @PathVariable int itemId,
            @RequestBody CommentDtoRequest dto
    ) {
        log.info("Пришел POST-запрос /items/{itemId={}}/comment, userId={} с телом={}", itemId, authorId, dto);
        CommentDtoResponse savedDto = commentMapper.commentToDtoResponse(itemService.createComment(dto, itemId, authorId));
        log.info("Ответ на POST-запрос /items/{itemId={}}/comment, userId={} с телом={}", itemId, authorId, savedDto);
        return savedDto;
    }
}
