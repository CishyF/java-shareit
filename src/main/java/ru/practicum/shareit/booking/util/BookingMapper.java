package ru.practicum.shareit.booking.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.ShortBookingDtoResponse;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.util.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code BookingMapper} class converts {@code BookingDtoRequest} to {@code Booking} with validating
 * and {@code Booking} to any type of Booking Request DTOs
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public abstract class BookingMapper {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ItemRepository itemRepository;

    public abstract BookingDtoResponse bookingToDtoResponse(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    public abstract ShortBookingDtoResponse bookingToShortDtoResponse(Booking booking);

    protected abstract Booking dtoRequestToBooking(BookingDtoRequest dto);

    public Booking dtoRequestToBooking(BookingDtoRequest dto, int itemId, int bookerId) {
        validateDatesOfDto(dto);
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка использования бронирования несуществующего пользователя"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка использования бронирования несуществующей вещи"));
        Booking booking = dtoRequestToBooking(dto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    private void validateDatesOfDto(BookingDtoRequest dto) {
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (end.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Попытка создания завершенной аренды");
        } else if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Попытка создания уже начавшейся аренды");
        } else if (end.isEqual(start)) {
            throw new IllegalArgumentException("Попытка создания аренды, которая начинается и заканчивается одновременно");
        } else if (end.isBefore(start)) {
            throw new IllegalArgumentException("Попытка создания аренды, которая начинается раньше, чем кончается");
        }
    }

    public List<BookingDtoResponse> bookingsToDtoResponses(List<Booking> bookings) {
        return bookings.stream().map(this::bookingToDtoResponse).collect(Collectors.toList());
    }
}
