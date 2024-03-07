package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.util.UserMapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Mock
    UserService mockUserService;

    @Mock
    UserMapper mockUserMapper;

    @InjectMocks
    UserController userController;

    final ObjectMapper mapper = new ObjectMapper();

    MockMvc mockMvc;

    UserDtoRequest dtoRequest;

    UserDtoResponse dtoResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
        dtoRequest = UserDtoRequest.builder()
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        dtoResponse = UserDtoResponse.builder()
                .id(1)
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
    }

    @Test
    void shouldCreateUser() throws Exception {
        lenient().when(mockUserService.create(dtoRequest))
                .thenReturn(new User());
        when(mockUserMapper.userToDtoResponse(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(dtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(dtoResponse.getEmail())));
    }

    @Test
    void shouldThrowIfDtoRequestIsInvalid() throws Exception {
        UserDtoRequest invalidDtoRequest = UserDtoRequest.builder()
                .name("  ")
                .email("  ")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(invalidDtoRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUsers() throws Exception {
        when(mockUserService.findAll())
                .thenReturn(Collections.emptyList());
        when(mockUserMapper.usersToDtoResponses(any()))
                .thenReturn(List.of(dtoResponse));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(dtoResponse.getName())))
                .andExpect(jsonPath("$[0].email", is(dtoResponse.getEmail())));
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(mockUserService.findById(1))
                .thenReturn(new User());
        when(mockUserMapper.userToDtoResponse(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(dtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(dtoResponse.getEmail())));
    }

    @Test
    void shouldPatchUpdateUser() throws Exception {
        lenient().when(mockUserService.update(1, dtoRequest))
                .thenReturn(new User());
        when(mockUserMapper.userToDtoResponse(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(dtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(dtoResponse.getEmail())));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(mockUserService, times(1))
                .delete(1);
    }
}
