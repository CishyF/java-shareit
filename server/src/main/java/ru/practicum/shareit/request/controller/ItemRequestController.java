package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.mapping.ItemRequestMapper;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;
    private final ItemRequestMapper requestMapper;

    @GetMapping
    public List<ItemRequestDtoResponse> getRequests(@RequestHeader("X-Sharer-User-Id") int requestorId) {
        log.info("Пришел GET-запрос /requests, userId={} без тела", requestorId);
        List<ItemRequestDtoResponse> dtos = requestMapper.itemRequestsToResponseDtos(
                requestService.findRequestsOfUser(requestorId)
        );
        log.info("Ответ на GET-запрос /requests, userId={} с телом={}", requestorId, dtos);
        return dtos;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getRequestsWithParams(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("Пришел GET-запрос /requests/all?from={}&size={}, userId={} без тела", from, size, userId);
        List<ItemRequestDtoResponse> dtos = requestMapper.itemRequestsToResponseDtos(
                requestService.findRequestsOfOtherUsers(userId, from, size)
        );
        log.info("Ответ на GET-запрос /requests/all?from={}&size={}, userId={} с телом={}", from, size, userId, dtos);
        return dtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequestById(@RequestHeader("X-Sharer-User-Id") int requestorId, @PathVariable int requestId) {
        log.info("Пришел GET-запрос /requests/{requestId={}}, userId={} без тела", requestId, requestorId);
        requestService.validateRequestor(requestorId);
        ItemRequestDtoResponse dto = requestMapper.itemRequestToResponseDto(requestService.findById(requestId));
        log.info("Ответ на GET-запрос /requests/{requestId={}}, userId={} с телом={}", requestId, requestorId, dto);
        return dto;
    }

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestHeader("X-Sharer-User-Id") int requestorId, @RequestBody ItemRequestDtoRequest dto) {
        log.info("Пришел POST-запрос /requests, userId={} с телом={}", requestorId, dto);
        ItemRequestDtoResponse savedDto = requestMapper.itemRequestToResponseDto(
                requestService.create(requestorId, dto)
        );
        log.info("Ответ на POST-запрос /requests, userId={} с телом={}", requestorId, savedDto);
        return savedDto;
    }
}
