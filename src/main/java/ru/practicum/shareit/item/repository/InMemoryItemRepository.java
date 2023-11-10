package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private int idCounter = 0;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        if (item.getId() != null && findById(item.getId()).isPresent()) {
            throw new EntityAlreadyExistsException("Предмет с таким id уже существует");
        }
        final int generatedItemId = ++idCounter;
        item.setId(generatedItemId);
        items.put(generatedItemId, item);
        return item;
    }

    @Override
    public Optional<Item> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findAllItemsOfUser(int ownerId) {
        return findAll().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }
}
