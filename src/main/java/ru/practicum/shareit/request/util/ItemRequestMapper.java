package ru.practicum.shareit.request.util;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemRequestMapper {

    ItemRequestDtoResponse itemRequestToResponseDto(ItemRequest request);

    default List<ItemRequestDtoResponse> itemRequestsToResponseDtos(List<ItemRequest> requests) {
        return requests.stream().map(this::itemRequestToResponseDto).collect(Collectors.toList());
    }

    ItemRequest dtoRequestToItemRequest(ItemRequestDtoRequest dto);

    default ItemRequest dtoRequestToItemRequest(ItemRequestDtoRequest dto, User requestor) {
        ItemRequest request = dtoRequestToItemRequest(dto);
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);
        return request;
    }
}
