package ru.practicum.shareit.user.util;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code UserMapper} class converts {@code UserDtoRequest} to {@code User} and {@code User} to {@code UserDtoResponse}
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDtoResponse userToDtoResponse(User user);

    User dtoRequestToUser(UserDtoRequest dto);

    default List<UserDtoResponse> usersToDtoResponses(List<User> users) {
        return users.stream().map(this::userToDtoResponse).collect(Collectors.toList());
    }
}