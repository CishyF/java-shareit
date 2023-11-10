package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.item.util.ItemPatchUpdater;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemPatchUpdater itemPatchUpdater;

    @Override
    public Item create(ItemDtoRequest itemDto, int ownerId) {
        Item item = itemMapper.dtoRequestToItem(itemDto, ownerId);
        return itemRepository.save(item);
    }

    @Override
    public List<Item> findAllItemsOfUser(int ownerId) {
        return itemRepository.findAllItemsOfUser(ownerId);
    }

    @Override
    public Item findById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получить несуществующий предмет"));
    }

    @Override
    public List<Item> findItemsContainingText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAll().stream()
                .filter(
                        item -> (StringUtils.containsIgnoreCase(item.getName(), text) ||
                                StringUtils.containsIgnoreCase(item.getDescription(), text)) &&
                                item.getAvailable()
                )
                .collect(Collectors.toList());
    }

    @Override
    public Item update(int ownerId, int id, ItemDtoRequest dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка обновить несуществующий предмет"));
        if (!isOwner(item, ownerId)) {
            throw new AccessException("Попытка обновить предмет, принадлежащий другому пользователю");
        }
        itemPatchUpdater.updateItem(item, dto);
        return item;
    }

    private boolean isOwner(Item item, int ownerId) {
        return item.getOwner().getId().equals(ownerId);
    }
}
