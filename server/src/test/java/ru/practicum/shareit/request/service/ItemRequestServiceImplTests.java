package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.mapping.ItemRequestMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTests {

    @Mock
    UserService mockUserService;

    @Mock
    ItemRequestRepository mockRequestRepo;

    @Mock
    ItemRequestMapper mockRequestMapper;

    @InjectMocks
    ItemRequestServiceImpl requestService;

    User requestor;

    ItemRequest expectedRequest;

    @BeforeEach
    void setup() {
        requestor = User.builder()
                .id(1)
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        expectedRequest = ItemRequest.builder()
                .id(1)
                .description("Описание")
                .requestor(requestor)
                .items(Collections.emptyList())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateRequest() {
        when(mockUserService.findById(1))
                .thenReturn(requestor);
        ItemRequestDtoRequest dto = ItemRequestDtoRequest.builder()
                        .description("Описание")
                        .build();
        when(mockRequestMapper.dtoRequestToItemRequest(dto, requestor))
                .thenReturn(expectedRequest);
        when(mockRequestRepo.save(expectedRequest))
                .thenReturn(expectedRequest);

        ItemRequest actualRequest = requestService.create(1, dto);

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockRequestMapper, times(1))
                .dtoRequestToItemRequest(dto, requestor);
        verify(mockRequestRepo, times(1))
                .save(expectedRequest);
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    void shouldFindRequestsOfUser() {
        when(mockUserService.findById(1))
                .thenReturn(requestor);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> expectedRequests = Collections.singletonList(expectedRequest);
        when(mockRequestRepo.findItemRequestsByRequestor(requestor, sort))
                .thenReturn(expectedRequests);

        List<ItemRequest> actualRequests = requestService.findRequestsOfUser(1);

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockRequestRepo, times(1))
                .findItemRequestsByRequestor(requestor, sort);
        assertEquals(expectedRequests, actualRequests);
    }

    @Test
    void shouldFindEmptyPageIfUserIsRequestor() {
        when(mockUserService.findById(1))
                .thenReturn(requestor);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<ItemRequest> expectedRequests = Collections.emptyList();
        when(mockRequestRepo.findItemRequestsByRequestorNotEqualUser(requestor, pageRequest))
                .thenReturn(Page.empty());

        List<ItemRequest> actualRequests = requestService.findRequestsOfOtherUsers(1, from, size);

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockRequestRepo, times(1))
                .findItemRequestsByRequestorNotEqualUser(requestor, pageRequest);
        assertEquals(expectedRequests, actualRequests);
    }

    @Test
    void shouldThrowIfFromIsNegative() {
        when(mockUserService.findById(1))
                .thenReturn(requestor);
        String expectedMessage = "Размер или элемент, с которого необходимо вернуть вещи, не должны быть меньше нуля";

        String actualMessage = assertThrows(
                 IllegalArgumentException.class,
                 () -> requestService.findRequestsOfOtherUsers(1, -1, 1)
        ).getMessage();
        verify(mockUserService, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowIfSizeIsLessThanOne() {
        when(mockUserService.findById(1))
                .thenReturn(requestor);
        String expectedMessage = "Размер или элемент, с которого необходимо вернуть вещи, не должны быть меньше нуля";

        String actualMessage = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.findRequestsOfOtherUsers(1, 0, 0)
        ).getMessage();

        verify(mockUserService, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindRequestById() {
        when(mockRequestRepo.findById(1))
                .thenReturn(Optional.of(expectedRequest));

        ItemRequest actualRequest = requestService.findById(1);

        verify(mockRequestRepo, times(1))
                .findById(1);
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    void shouldThrowIfRequestWithIdDoesNotExist() {
        when(mockRequestRepo.findById(2))
                .thenReturn(Optional.empty());
        String expectedMessage = "Попытка получения несуществующего запроса";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> requestService.findById(2)
        ).getMessage();

        verify(mockRequestRepo, times(1))
                .findById(2);
        assertEquals(expectedMessage, actualMessage);
    }

}
