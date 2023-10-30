package ru.practicum.shareit.user.util;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code UserMapper} class converts {@code UserDto} to {@code User} and vice versa
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User dtoToUser(UserDto dto);

    default List<UserDto> usersToDtos(List<User> users) {
        return users.stream().map(this::userToDto).collect(Collectors.toList());
    }
}