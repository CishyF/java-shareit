package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.RequestBookingStates;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.List;

public interface BookingService {

    Booking create(BookingDtoRequest dto, int bookerId);

    List<Booking> findBookingsOfItemsOwnerByState(int itemsOwnerId, RequestBookingStates state, int from, int size);

    List<Booking> findBookingsOfUserByState(int bookerId, RequestBookingStates state, int from, int size);

    Booking findById(int id, int userId);

    Booking update(int id, boolean isApproved, int userId);
}
