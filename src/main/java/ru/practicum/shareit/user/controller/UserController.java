package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.util.UserMapper;

import javax.validation.Valid;
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
        log.info("Пришел GET-запрос /users без тела");
        Collection<UserDtoResponse> userDtos = userMapper.usersToDtoResponses(userService.findAll());
        log.info("Ответ на GET-запрос /users с телом={}", userDtos);
        return userDtos;
    }

    @GetMapping("/{id}")
    public UserDtoResponse getUserById(@PathVariable int id) {
        log.info("Пришел GET-запрос /users/{id={}} без тела", id);
        UserDtoResponse userDto = userMapper.userToDtoResponse(userService.findById(id));
        log.info("Ответ на GET-запрос /users/{id={}} с телом={}", id, userDto);
        return userDto;
    }

    @PostMapping
    public UserDtoResponse createUser(@RequestBody @Valid UserDtoRequest userDto) {
        log.info("Пришел POST-запрос /users с телом={}", userDto);
        UserDtoResponse createdUserDto = userMapper.userToDtoResponse(userService.create(userDto));
        log.info("Ответ на POST-запрос /users с телом={}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{id}")
    public UserDtoResponse patchUser(
            @PathVariable int id,
            @RequestBody UserDtoRequest dto
    ) {
        log.info("Пришел PATCH-запрос /users/{id={}} с телом={}", id, dto);
        UserDtoResponse updatedUserDto = userMapper.userToDtoResponse(userService.update(id, dto));
        log.info("Ответ на PATCH-запрос /users/{id={}} с телом={}", id, updatedUserDto);
        return updatedUserDto;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Пришел DELETE-запрос /users/{id={}} без тела", id);
        userService.delete(id);
        log.info("Ответ на DELETE-запрос /users/{id={}} без тела", id);
    }
}
