package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest create(int requestorId, ItemRequestDtoRequest dto);

    List<ItemRequest> findRequestsOfUser(int requestorId);

    List<ItemRequest> findRequestsOfOtherUsers(int userId, int from, int size);

    ItemRequest findById(int requestId);
}
