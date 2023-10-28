package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
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
    public ItemDto create(ItemDto itemDto, int ownerId) {
        Item item = itemMapper.dtoToItem(itemDto);
        item.setOwnerId(ownerId);
        Item savedItem = itemRepository.save(item);
        return itemMapper.itemToDto(savedItem);
    }

    @Override
    public List<ItemDto> findAllItemsOfUser(int ownerId) {
        List<Item> itemsOfUser = itemRepository.findAllItemsOfUser(ownerId);
        return itemMapper.itemsToDtos(itemsOfUser);
    }

    @Override
    public ItemDto findById(int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получить несуществующий предмет"));
        return itemMapper.itemToDto(item);
    }

    @Override
    public List<ItemDto> findItemsContainingText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemsContainingText = itemRepository.findAll().stream()
                .filter(
                        item -> (StringUtils.containsIgnoreCase(item.getName(), text) ||
                                StringUtils.containsIgnoreCase(item.getDescription(), text)) &&
                                item.getAvailable()
                )
                .collect(Collectors.toList());
        return itemMapper.itemsToDtos(itemsContainingText);
    }

    @Override
    public ItemDto update(int ownerId, int id, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка обновить несуществующий предмет"));
        if (!isOwner(item, ownerId)) {
            throw new AccessException("Попытка обновить предмет, принадлежащий другому пользователю");
        }
        itemPatchUpdater.updateItem(item, dto);
        return itemMapper.itemToDto(item);
    }

    private boolean isOwner(Item item, int ownerId) {
        return item.getOwnerId().equals(ownerId);
    }
}
