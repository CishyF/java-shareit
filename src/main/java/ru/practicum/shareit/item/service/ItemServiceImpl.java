package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
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
    public ItemDtoResponse create(ItemDtoRequest itemDto, int ownerId) {
        Item item = itemMapper.dtoRequestToItem(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);
        return itemMapper.itemToDtoResponse(savedItem);
    }

    @Override
    public List<ItemDtoResponse> findAllItemsOfUser(int ownerId) {
        List<Item> itemsOfUser = itemRepository.findAllItemsOfUser(ownerId);
        return itemMapper.itemsToDtoResponses(itemsOfUser);
    }

    @Override
    public ItemDtoResponse findById(int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получить несуществующий предмет"));
        return itemMapper.itemToDtoResponse(item);
    }

    @Override
    public List<ItemDtoResponse> findItemsContainingText(String text) {
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
        return itemMapper.itemsToDtoResponses(itemsContainingText);
    }

    @Override
    public ItemDtoResponse update(int ownerId, int id, ItemDtoRequest dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка обновить несуществующий предмет"));
        if (!isOwner(item, ownerId)) {
            throw new AccessException("Попытка обновить предмет, принадлежащий другому пользователю");
        }
        itemPatchUpdater.updateItem(item, dto);
        return itemMapper.itemToDtoResponse(item);
    }

    private boolean isOwner(Item item, int ownerId) {
        return item.getOwner().getId().equals(ownerId);
    }
}
