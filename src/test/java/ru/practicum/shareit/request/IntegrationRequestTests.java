package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.util.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "db.name=request_test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationRequestTests {

    final UserService userService;

    final ItemMapper itemMapper;

    final ItemRequestMapper requestMapper;

    final ItemRequestService requestService;

    List<User> expectedRequestors = new ArrayList<>();

    List<ItemRequest> expectedRequests = new ArrayList<>();

    @BeforeEach
    void setup() {
        UserDtoRequest requestorDto1 = UserDtoRequest.builder()
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        UserDtoRequest requestorDto2 = UserDtoRequest.builder()
                .name("John")
                .email("John@gmail.com")
                .build();
        User requestor1 = userService.create(requestorDto1);
        User requestor2 = userService.create(requestorDto2);
        expectedRequestors.add(requestor1);
        expectedRequestors.add(requestor2);

        ItemRequestDtoRequest requestDto1 = ItemRequestDtoRequest.builder()
                .description("Нужен шуруповерт")
                .build();
        ItemRequestDtoRequest requestDto2 = ItemRequestDtoRequest.builder()
                .description("Возьму в аренду вантуз")
                .build();
        ItemRequest request1 = requestService.create(requestor1.getId(), requestDto1);
        ItemRequest request2 = requestService.create(requestor2.getId(), requestDto2);
        expectedRequests.add(request1);
        expectedRequests.add(request2);
    }

    @Test
    void shouldFindRequestsOfUsers() {
        User requestor1 = expectedRequestors.get(0);
        User requestor2 = expectedRequestors.get(1);
        List<ItemRequest> expectedRequestsForRequestor1 = List.of(expectedRequests.get(0));
        List<ItemRequest> expectedRequestsForRequestor2 = List.of(expectedRequests.get(1));

        List<ItemRequest> actualRequestsForRequestor1 = requestService.findRequestsOfUser(requestor1.getId());
        List<ItemRequest> actualRequestsForRequestor2 = requestService.findRequestsOfUser(requestor2.getId());

        assertEquals(expectedRequestsForRequestor1, actualRequestsForRequestor1);
        assertEquals(expectedRequestsForRequestor2, actualRequestsForRequestor2);
    }

    @Test
    void shouldFindRequestsOfOtherUsers() {
        User requestor1 = expectedRequestors.get(0);
        User requestor2 = expectedRequestors.get(1);
        List<ItemRequest> expectedRequestsForRequestor1 = List.of(expectedRequests.get(1));
        List<ItemRequest> expectedRequestsForRequestor2 = List.of(expectedRequests.get(0));

        List<ItemRequest> actualRequestsForRequestor1 = requestService.findRequestsOfOtherUsers(requestor1.getId(), 0, 1);
        List<ItemRequest> actualRequestsForRequestor2 = requestService.findRequestsOfOtherUsers(requestor2.getId(), 0, 1);

        assertEquals(expectedRequestsForRequestor1, actualRequestsForRequestor1);
        assertEquals(expectedRequestsForRequestor2, actualRequestsForRequestor2);
    }

    @Test
    void shouldMakeRequestDtoResponseCorrectly() {
        ItemRequest request1 = expectedRequests.get(0);
        List<ItemDtoResponse> itemDtoResponses = itemMapper.itemsToDtoResponses(request1.getItems());
        ItemRequestDtoResponse expectedRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(request1.getId())
                .description(request1.getDescription())
                .created(request1.getCreated())
                .items(itemDtoResponses)
                .build();

        ItemRequestDtoResponse actualRequestDtoResponse = requestMapper.itemRequestToResponseDto(request1);

        assertEquals(expectedRequestDtoResponse, actualRequestDtoResponse);
    }

}
