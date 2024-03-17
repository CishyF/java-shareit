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
        return requestMapper.itemRequestsToResponseDtos(
                requestService.findRequestsOfUser(requestorId)
        );
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getRequestsWithParams(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam int from,
            @RequestParam int size
    ) {
        return requestMapper.itemRequestsToResponseDtos(
                requestService.findRequestsOfOtherUsers(userId, from, size)
        );
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequestById(@RequestHeader("X-Sharer-User-Id") int requestorId, @PathVariable int requestId) {
        requestService.validateRequestor(requestorId);
        return requestMapper.itemRequestToResponseDto(requestService.findById(requestId));
    }

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestHeader("X-Sharer-User-Id") int requestorId, @RequestBody ItemRequestDtoRequest dto) {
        return requestMapper.itemRequestToResponseDto(
                requestService.create(requestorId, dto)
        );
    }
}
