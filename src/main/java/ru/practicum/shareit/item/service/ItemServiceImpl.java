package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.ShortBookingDtoResponse;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.EntityIsNotAvailableException;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.util.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.item.util.ItemPatchUpdater;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemPatchUpdater itemPatchUpdater;

    @Override
    public Item create(ItemDtoRequest itemDto, int ownerId) {
        Item item = itemMapper.dtoRequestToItem(itemDto, ownerId);
        return itemRepository.save(item);
    }

    @Override
    public List<Item> findItemsOfUser(int ownerId) {
        User owner = userService.findById(ownerId);
        return itemRepository.findItemsByOwnerOrderById(owner);
    }

    @Override
    public List<LongItemDtoResponse> findLongItemDtosOfUser(int ownerId) {
        List<Item> items = findItemsOfUser(ownerId);
        return items.stream().map(this::makeLongItemDto).collect(Collectors.toList());
    }

    @Override
    public Item findById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получить несуществующий предмет"));
    }

    @Override
    public LongItemDtoResponse findLongItemDtoById(int id, int userId) {
        Item item = findById(id);
        int ownerId = item.getOwner().getId();
        if (ownerId != userId) {
            return itemMapper.itemToLongDtoResponse(item, null, null);
        }
        return makeLongItemDto(item);
    }

    private LongItemDtoResponse makeLongItemDto(Item item) {
        Pageable pageable = PageRequest.of(0, 1);

        Booking lastBooking = bookingRepository.findLastBookingByItem(item, pageable)
                .stream().findFirst().orElse(null);
        Booking nextBooking = bookingRepository.findNextBookingByItem(item, pageable)
                .stream().findFirst().orElse(null);

        ShortBookingDtoResponse lastBookingDto = bookingMapper.bookingToShortDtoResponse(lastBooking);
        ShortBookingDtoResponse nextBookingDto = bookingMapper.bookingToShortDtoResponse(nextBooking);

        return itemMapper.itemToLongDtoResponse(item, lastBookingDto, nextBookingDto);
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
    public Item update(ItemDtoRequest dto, int id, int ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка обновить несуществующий предмет"));
        if (!isOwner(item, ownerId)) {
            throw new AccessException("Попытка обновить предмет, принадлежащий другому пользователю");
        }
        itemPatchUpdater.updateItem(item, dto);
        itemRepository.save(item);
        return item;
    }

    private boolean isOwner(Item item, int ownerId) {
        return item.getOwner().getId().equals(ownerId);
    }

    @Override
    public Comment createComment(CommentDtoRequest dto, int itemId, int authorId) {
        User author = userService.findById(authorId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка оставить комментарий у несуществующей вещи"));
        if (bookingRepository.findBookingsByBooker(author).stream()
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED &&
                                    booking.getStart().isBefore(LocalDateTime.now())
                )
                .map(Booking::getItem)
                .noneMatch(item::equals)
        ) {
            throw new EntityIsNotAvailableException("У данного пользователя нет прав для оставления комментария у этой вещи");
        }

        Comment comment = commentMapper.dtoRequestToComment(dto, itemId, authorId);
        return commentRepository.save(comment);
    }
}
