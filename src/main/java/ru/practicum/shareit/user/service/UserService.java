package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User create(UserDtoRequest userDto);

    List<User> findAll();

    User findById(int id);

    User update(int id, UserDtoRequest dto);

    void delete(int id);

    boolean isEmailExists(String email);
}
