package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.PatchUserDto;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Пришел GET-запрос /users без тела");
        ResponseEntity<Object> response = userClient.getUsers();
        log.info("Ответ на GET-запрос /users с телом={}", response);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        log.info("Пришел GET-запрос /users/{id={}} без тела", id);
        ResponseEntity<Object> response = userClient.getUserById(id);
        log.info("Ответ на GET-запрос /users/{id={}} с телом={}", id, response);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserDto dto) {
        log.info("Пришел POST-запрос /users с телом={}", dto);
        ResponseEntity<Object> response = userClient.createUser(dto);
        log.info("Ответ на POST-запрос /users с телом={}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable int id,
            @RequestBody PatchUserDto dto) {
        log.info("Пришел PATCH-запрос /users/{id={}} с телом={}", id, dto);
        ResponseEntity<Object> response = userClient.patchUser(id, dto);
        log.info("Ответ на PATCH-запрос /users/{id={}} с телом={}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        log.info("Пришел DELETE-запрос /users/{id={}} без тела", id);
        ResponseEntity<Object> response = userClient.deleteUser(id);
        log.info("Ответ на DELETE-запрос /users/{id={}} с телом={}", id, response);
        return response;
    }

}
