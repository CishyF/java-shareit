package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") int userId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("Пришел GET-запрос /items?from={}&size={}, userId={} без тела", from, size, userId);
        ResponseEntity<Object> response = itemClient.getItemsOfUser(userId, from, size);
        log.info("Ответ на GET-запрос /items?from={}&size={}, userId={} с телом={}", from, size, userId, response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int itemId) {
        log.info("Пришел GET-запрос /items/{itemId={}}, userId={} без тела", itemId, userId);
        ResponseEntity<Object> response = itemClient.getItemById(userId, itemId);
        log.info("Ответ на GET-запрос /items/{itemId={}}, userId={} с телом={}", itemId, userId, response);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsContainingText(@RequestHeader("X-Sharer-User-Id") int userId,
            @NotNull @RequestParam(value = "text", required = false) String text,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("Пришел GET-запрос /items/search?text={}&from={}&size={}, userId={} без тела", text, from, size, userId);
        ResponseEntity<Object> response = itemClient.getItemsContainingText(userId, text, from, size);
        log.info("Ответ на GET-запрос /items/search?text={}&from={}&size={}, userId={} с телом={}", text, from, size, userId, response);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody @Valid CreateItemDto dto) {
        log.info("Пришел POST-запрос /items, userId={} с телом={}", userId, dto);
        ResponseEntity<Object> response = itemClient.createItem(userId, dto);
        log.info("Ответ на POST-запрос /items, userId={} с телом={}", userId, response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int itemId,
            @RequestBody PatchItemDto dto
    ) {
        log.info("Пришел PATCH-запрос /items/{itemId={}}, userId={} с телом={}", itemId, userId, dto);
        ResponseEntity<Object> response = itemClient.patchItem(userId, itemId, dto);
        log.info("Ответ на PATCH-запрос /items/{itemId={}}, userId={} с телом={}", itemId, userId, response);
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int itemId, @RequestBody @Valid CreateCommentDto dto) {
        log.info("Пришел POST-запрос /items/{itemId={}}/comment, userId={} с телом={}", itemId, userId, dto);
        ResponseEntity<Object> response = itemClient.createComment(userId, itemId, dto);
        log.info("Ответ на POST-запрос /items/{itemId={}}/comment, userId={} с телом={}", itemId, userId, response);
        return response;
    }

}
