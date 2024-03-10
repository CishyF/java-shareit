package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.util.ItemRequestMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTests {

    @Mock
    UserService mockUserService;

    @Mock
    ItemRequestService mockRequestService;

    @Mock
    ItemRequestMapper mockRequestMapper;

    @InjectMocks
    ItemRequestController requestController;

    final ObjectMapper mapper = new ObjectMapper();

    MockMvc mockMvc;

    ItemRequestDtoRequest dtoRequest;

    ItemRequestDtoResponse dtoResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
        dtoRequest = ItemRequestDtoRequest.builder()
                .description("Хочу кастрюлю")
                .build();
        dtoResponse = ItemRequestDtoResponse.builder()
                .id(1)
                .created(LocalDateTime.now())
                .description("Хочу кастрюлю")
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void shouldCreateRequest() throws Exception {
        lenient().when(mockRequestService.create(1, dtoRequest))
                .thenReturn(new ItemRequest());
        when(mockRequestMapper.itemRequestToResponseDto(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(dtoResponse.getCreated().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.description", is(dtoResponse.getDescription())))
                .andExpect(jsonPath("$.items", is(dtoResponse.getItems()), List.class));
    }

    @Test
    void shouldThrowIfDtoRequestIsInvalid() throws Exception {
        ItemRequestDtoRequest invalidDtoRequest = ItemRequestDtoRequest.builder()
                        .description("  ")
                        .build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(invalidDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetRequests() throws Exception {
        when(mockRequestService.findRequestsOfUser(1))
                .thenReturn(Collections.emptyList());
        when(mockRequestMapper.itemRequestsToResponseDtos(any()))
                .thenReturn(List.of(dtoResponse));

        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(dtoResponse.getCreated().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].description", is(dtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].items", is(dtoResponse.getItems()), List.class));
    }

    @Test
    void shouldGetRequestsWithDefaultParams() throws Exception {
        when(mockRequestService.findRequestsOfOtherUsers(1, 0, 10))
                .thenReturn(Collections.emptyList());
        when(mockRequestMapper.itemRequestsToResponseDtos(any()))
                .thenReturn(List.of(dtoResponse));

        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(dtoResponse.getCreated().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].description", is(dtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].items", is(dtoResponse.getItems()), List.class));
    }

    @Test
    void shouldGetEmptyArrayWithCustomParams() throws Exception {
        int from = 2, size = 1;
        when(mockRequestService.findRequestsOfOtherUsers(1, from,  size))
                .thenReturn(Collections.emptyList());
        when(mockRequestMapper.itemRequestsToResponseDtos(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(String.format("/requests/all?from=%d&size=%d", from, size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(mockRequestService.findById(1))
                .thenReturn(new ItemRequest());
        when(mockRequestMapper.itemRequestToResponseDto(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(dtoResponse.getCreated().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.description", is(dtoResponse.getDescription())))
                .andExpect(jsonPath("$.items", is(dtoResponse.getItems()), List.class));
    }

}
