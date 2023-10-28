package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Пришел GET-запрос /users без тела");
        Collection<UserDto> userDtos = userService.findAll();
        log.info("Ответ на GET-запрос /users с телом={}", userDtos);
        return userDtos;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.info("Пришел GET-запрос /users/{id={}} без тела", id);
        UserDto userDto = userService.findById(id);
        log.info("Ответ на GET-запрос /users/{id={}} с телом={}", id, userDto);
        return userDto;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Пришел POST-запрос /users с телом={}", userDto);
        UserDto createdUserDto = userService.create(userDto);
        log.info("Ответ на POST-запрос /users с телом={}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(
            @PathVariable int id,
            @RequestBody UserDto dto
    ) {
        log.info("Пришел PATCH-запрос /users/{id={}} с телом={}", id, dto);
        UserDto updatedUserDto = userService.update(id, dto);
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
