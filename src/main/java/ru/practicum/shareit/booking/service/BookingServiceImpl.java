package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.RequestBookingStates;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.exception.BookingByOwnerOfItemException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.EntityIsNotAvailableException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private static final Sort ORDER_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public Booking create(BookingDtoRequest dto, int bookerId) {
        int itemId = dto.getItemId() == null ? -1 : dto.getItemId();

        User booker = userService.findById(bookerId);
        Item item = itemService.findById(itemId);
        Booking booking = bookingMapper.dtoRequestToBooking(dto, item, booker);

        int ownerIdOfBookedItem = booking.getItem().getOwner().getId();
        if (ownerIdOfBookedItem == bookerId) {
            throw new BookingByOwnerOfItemException("Попытка арендовать свою вещь");
        }

        if (booking.getItem().getAvailable()) {
            return bookingRepository.save(booking);
        } else {
            throw new EntityIsNotAvailableException("Аренда с этим предметом уже существует");
        }
    }

    @Override
    public List<Booking> findBookingsOfBookerItemsByState(int bookerId, RequestBookingStates state) {
        List<Item> itemsOfBooker = itemService.findItemsOfUser(bookerId);
        final LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByItemIn(itemsOfBooker, ORDER_BY_START_DESC);
            case CURRENT:
                return bookingRepository.findCurrentBookingsByItemIn(itemsOfBooker, ORDER_BY_START_DESC);
            case PAST:
                return bookingRepository.findBookingsByItemInAndEndIsBefore(itemsOfBooker, now, ORDER_BY_START_DESC);
            case FUTURE:
                return bookingRepository.findBookingsByItemInAndStartIsAfter(itemsOfBooker, now, ORDER_BY_START_DESC);
        }
        BookingStatus waitingOrRejected = BookingStatus.valueOf(state.name());
        return bookingRepository.findBookingsByItemInAndStatus(
                itemsOfBooker, waitingOrRejected, ORDER_BY_START_DESC
        );
    }

    @Override
    public List<Booking> findBookingsOfUserByState(int bookerId, RequestBookingStates state) {
        User booker = userService.findById(bookerId);
        final LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByBooker(booker, ORDER_BY_START_DESC);
            case CURRENT:
                return bookingRepository.findCurrentBookingsByBooker(booker, ORDER_BY_START_DESC);
            case PAST:
                return bookingRepository.findBookingsByBookerAndEndIsBefore(booker, now, ORDER_BY_START_DESC);
            case FUTURE:
                return bookingRepository.findBookingsByBookerAndStartIsAfter(booker, now, ORDER_BY_START_DESC);
        }
        BookingStatus waitingOrRejected = BookingStatus.valueOf(state.name());
        return bookingRepository.findBookingsByBookerAndStatus(
                booker, waitingOrRejected, ORDER_BY_START_DESC
        );
    }

    @Override
    public Booking findById(int id, int userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получить несуществующую аренду"));

        int ownerIdOfBookedItem = booking.getItem().getOwner().getId();
        int bookerId = booking.getBooker().getId();
        if (bookerId == userId || ownerIdOfBookedItem == userId) {
            return booking;
        }
        throw new EntityDoesNotExistException("Данной аренды нет у такого пользователя");
    }

    @Override
    public Booking update(int id, boolean isApproved, int userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка обновить статус несуществующей аренды"));

        checkUpdatePermissions(booking, userId);

        if (BookingStatus.WAITING != booking.getStatus()) {
            throw new IllegalArgumentException("Попытка обновить ранее установленный статус");
        } else if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    private void checkUpdatePermissions(Booking booking, int userId) {
        int bookerId = booking.getBooker().getId();
        if (bookerId == userId) {
            throw new EntityDoesNotExistException("Данной аренды нет у такого пользователя");
        }
        int ownerIdOfBookedItem = booking.getItem().getOwner().getId();
        if (ownerIdOfBookedItem != userId) {
            throw new EntityIsNotAvailableException("Аренда может обновляться только владельцем вещи");
        }
    }
}
