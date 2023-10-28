package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public Collection<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("Пришел GET-запрос /items без тела");
        Collection<ItemDto> itemsDto = itemService.findAllItemsOfUser(ownerId);
        log.info("Ответ на GET-запрос /items с телом={}", itemsDto);
        return itemsDto;
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable int id) {
        log.info("Пришел GET-запрос /items/{id={}} без тела", id);
        ItemDto itemDto = itemService.findById(id);
        log.info("Ответ на GET-запрос /items/{id={}} с телом={}", id, itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsContainingText(@RequestParam(value = "text", required = false) String text) {
        log.info("Пришел GET-запрос /items/search?text={} без тела", text);
        Collection<ItemDto> itemDtos = itemService.findItemsContainingText(text);
        log.info("Ответ на GET-запрос /items/search?text={} с телом={}", text, itemDtos);
        return itemDtos;
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @RequestBody @Valid ItemDto dto
    ) {
        log.info("Пришел POST-запрос /items с телом={}", dto);
        userService.findById(ownerId);
        ItemDto savedDto = itemService.create(dto, ownerId);
        log.info("Ответ на POST-запрос /items с телом={}", savedDto);
        return savedDto;
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @PathVariable int id,
            @RequestBody ItemDto dto
    ) {
        log.info("Пришел PATCH-запрос /items/{id={}} с телом={}", id, dto);
        ItemDto updatedDto = itemService.update(ownerId, id, dto);
        log.info("Ответ на PATCH-запрос /items/{id={}} с телом={}", id, updatedDto);
        return updatedDto;
    }
}
