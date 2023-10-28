package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(int id);

    List<Item> findAll();

    List<Item> findAllItemsOfUser(int ownerId);
}
