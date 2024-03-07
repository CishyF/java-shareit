package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.util.ItemRequestMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;
    private final UserService userService;
    private final ItemRequestMapper requestMapper;

    @GetMapping
    public List<ItemRequestDtoResponse> getRequests(@RequestHeader("X-Sharer-User-Id") int requestorId) {
        log.info("Пришел GET-запрос /requests без тела");
        List<ItemRequestDtoResponse> dtos = requestMapper.itemRequestsToResponseDtos(
                requestService.findRequestsOfUser(requestorId)
        );
        log.info("Ответ на GET-запрос /requests с телом={}", dtos);
        return dtos;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getRequestsWithParams(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("Пришел GET-запрос /requests/all?from={}&size={} без тела", from, size);
        List<ItemRequestDtoResponse> dtos = requestMapper.itemRequestsToResponseDtos(
                requestService.findRequestsOfOtherUsers(userId, from, size)
        );
        log.info("Ответ на GET-запрос /requests/all?from={}&size={} с телом={}", from, size, dtos);
        return dtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequest(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int requestId) {
        log.info("Пришел GET-запрос /requests/{requestId={}} без тела", requestId);
        userService.findById(userId);
        ItemRequestDtoResponse dto = requestMapper.itemRequestToResponseDto(requestService.findById(requestId));
        log.info("Ответ на GET-запрос /requests/{requestId={}} с телом={}", requestId, dto);
        return dto;
    }

    @PostMapping
    public ItemRequestDtoResponse createRequest(
            @RequestHeader("X-Sharer-User-Id") int requestorId, @Valid @RequestBody ItemRequestDtoRequest dto
    ) {
        log.info("Пришел POST-запрос /requests с телом={}", dto);
        ItemRequestDtoResponse savedDto = requestMapper.itemRequestToResponseDto(
                requestService.create(requestorId, dto)
        );
        log.info("Ответ на POST-запрос /requests с телом={}", savedDto);
        return savedDto;
    }
}
