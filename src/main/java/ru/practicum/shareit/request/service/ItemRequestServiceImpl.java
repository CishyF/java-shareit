package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.mapping.ItemRequestMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private static final Sort ORDER_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    private final ItemRequestMapper requestMapper;
    private final ItemRequestRepository requestRepository;
    private final UserService userService;

    @Override
    public ItemRequest create(int requestorId, ItemRequestDtoRequest dto) {
        User requestor = userService.findById(requestorId);
        ItemRequest request = requestMapper.dtoRequestToItemRequest(dto, requestor);
        return requestRepository.save(request);
    }

    @Override
    public List<ItemRequest> findRequestsOfUser(int requestorId) {
        User requestor = userService.findById(requestorId);
        return requestRepository.findItemRequestsByRequestor(requestor, ORDER_BY_CREATED_DESC);
    }

    @Override
    public List<ItemRequest> findRequestsOfOtherUsers(int userId, int from, int size) {
        final User user = userService.findById(userId);
        final int page = getPage(from, size);
        final PageRequest pageRequest = PageRequest.of(page, size, ORDER_BY_CREATED_DESC);
        return requestRepository.findItemRequestsByRequestorNotEqualUser(user, pageRequest).getContent();
    }

    private int getPage(int fromElement, int size) {
        if (fromElement < 0 || size < 1) {
            throw new IllegalArgumentException("Размер или элемент, с которого необходимо вернуть вещи, не должны быть меньше нуля");
        }
        return fromElement / size;
    }

    @Override
    public ItemRequest findById(int requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получения несуществующего запроса"));
    }

    @Override
    public void validateRequestor(int requestorId) {
        try {
            userService.findById(requestorId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityDoesNotExistException("Попытка получения запроса несуществующего пользователя");
        }
    }
}
