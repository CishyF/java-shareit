package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Пришел GET-запрос /requests, userId={} без тела", userId);
        ResponseEntity<Object> response = requestClient.getRequests(userId);
        log.info("Ответ на GET-запрос /requests, userId={} с телом={}", userId, response);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsWithParams(@RequestHeader("X-Sharer-User-Id") int userId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Пришел GET-запрос /requests/all?from={}&size={}, userId={} без тела", from, size, userId);
        ResponseEntity<Object> response = requestClient.getRequestsWithParams(userId, from, size);
        log.info("Ответ на GET-запрос /requests/all?from={}&size={}, userId={} с телом={}", from, size, userId, response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int requestId) {
        log.info("Пришел GET-запрос /requests/{requestId={}}, userId={} без тела", requestId, userId);
        ResponseEntity<Object> response = requestClient.getRequestById(userId, requestId);
        log.info("Ответ на GET-запрос /requests/{requestId={}}, userId={} с телом={}", requestId, userId, response);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody @Valid CreateItemRequestDto dto) {
        log.info("Пришел POST-запрос /requests, userId={} с телом={}", userId, dto);
        ResponseEntity<Object> response = requestClient.createRequest(userId, dto);
        log.info("Ответ на POST-запрос /requests, userId={} с телом={}", userId, response);
        return response;
    }

}
