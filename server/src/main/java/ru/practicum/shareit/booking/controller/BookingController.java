package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.RequestBookingStates;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.mapping.BookingMapper;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping
    public Collection<BookingDtoResponse> getBookingsOfUserByState(
            @RequestHeader("X-Sharer-User-Id") int bookerId,
            @RequestParam RequestBookingStates state,
            @RequestParam int from,
            @RequestParam int size
    ) {
        return bookingMapper.bookingsToDtoResponses(
                bookingService.findBookingsOfUserByState(bookerId, state, from, size)
        );
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getBookingsOfItemsOwnerByState(
            @RequestHeader("X-Sharer-User-Id") int itemsOwnerId,
            @RequestParam RequestBookingStates state,
            @RequestParam int from,
            @RequestParam int size
    ) {
        return bookingMapper.bookingsToDtoResponses(
                bookingService.findBookingsOfItemsOwnerByState(itemsOwnerId, state, from, size)
        );
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int bookingId
    ) {
        return bookingMapper.bookingToDtoResponse(bookingService.findById(bookingId, userId));
    }

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestHeader("X-Sharer-User-Id") int bookerId,
            @RequestBody BookingDtoRequest dto
    ) {
        return bookingMapper.bookingToDtoResponse(bookingService.create(dto, bookerId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse patchBooking(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int bookingId,
            @RequestParam(name = "approved") boolean isApproved
    ) {
        return bookingMapper.bookingToDtoResponse(bookingService.update(bookingId, isApproved, userId));
    }
}
