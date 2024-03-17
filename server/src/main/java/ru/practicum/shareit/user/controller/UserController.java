package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mapping.UserMapper;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public Collection<UserDtoResponse> getUsers() {
        return userMapper.usersToDtoResponses(userService.findAll());
    }

    @GetMapping("/{id}")
    public UserDtoResponse getUserById(@PathVariable int id) {
        return userMapper.userToDtoResponse(userService.findById(id));
    }

    @PostMapping
    public UserDtoResponse createUser(@RequestBody UserDtoRequest userDto) {
        return userMapper.userToDtoResponse(userService.create(userDto));
    }

    @PatchMapping("/{id}")
    public UserDtoResponse patchUser(@PathVariable int id, @RequestBody UserDtoRequest dto) {
        return userMapper.userToDtoResponse(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.delete(id);
    }

}
