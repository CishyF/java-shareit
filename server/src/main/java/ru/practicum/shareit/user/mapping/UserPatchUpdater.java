package ru.practicum.shareit.user.mapping;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.entity.User;

/**
 * The {@code UserPatchUpdater} class updates field(-s) of got {@code User} instance
 *  according to {@code UserDtoRequest} field(-s)
 */
@Mapper(componentModel = "spring")
public interface UserPatchUpdater {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserDtoRequest userDto);
}
