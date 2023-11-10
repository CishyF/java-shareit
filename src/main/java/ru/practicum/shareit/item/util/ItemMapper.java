package ru.practicum.shareit.item.util;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.util.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code ItemMapper} class converts {@code ItemDto} to {@code Item} and vice versa
 */
@Mapper(componentModel = "spring", uses=UserMapper.class)
public abstract class ItemMapper {

    @Autowired
    protected UserService userService;

    public abstract ItemDto itemToDto(Item item);

    public abstract Item dtoToItem(ItemDto dto);

    public Item dtoToItem(ItemDto dto, Integer ownerId) {
        if (ownerId == null || ownerId < 1) {
            throw new IllegalArgumentException("Попытка получения вещи у несуществующего пользователя");
        }
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        User owner = userMapper.dtoToUser(userService.findById(ownerId));
        Item item = dtoToItem(dto);
        item.setOwner(owner);
        return item;
    }

    public List<ItemDto> itemsToDtos(List<Item> items) {
        return items.stream().map(this::itemToDto).collect(Collectors.toList());
    }
}
