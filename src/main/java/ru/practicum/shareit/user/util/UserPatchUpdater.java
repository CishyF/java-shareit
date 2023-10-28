package ru.practicum.shareit.user.util;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;

/**
 * The {@code UserPatchUpdater} class updates field(-s) of got {@code User} instance
 *  according to {@code UserDto} field(-s)
 */
@Mapper(componentModel = "spring")
public interface UserPatchUpdater {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserDto userDto);
}
