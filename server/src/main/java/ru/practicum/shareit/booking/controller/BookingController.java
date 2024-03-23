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
        log.info("Пришел GET-запрос /bookings?state={}&from={}&size={}, userId={} без тела", state, from, size, bookerId);
        Collection<BookingDtoResponse> bookings = bookingMapper.bookingsToDtoResponses(
                bookingService.findBookingsOfUserByState(bookerId, state, from, size)
        );
        log.info("Ответ на GET-запрос /bookings?state={}&from={}&size={}, userId={} с телом={}", state, from, size, bookerId, bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getBookingsOfItemsOwnerByState(
            @RequestHeader("X-Sharer-User-Id") int itemsOwnerId,
            @RequestParam RequestBookingStates state,
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("Пришел GET-запрос /bookings/owner?state={}&from={}&size={}, userId={} без тела", state, from, size, itemsOwnerId);
        Collection<BookingDtoResponse> bookings = bookingMapper.bookingsToDtoResponses(
                bookingService.findBookingsOfItemsOwnerByState(itemsOwnerId, state, from, size)
        );
        log.info("Ответ на GET-запрос /bookings/owner?state={}&from={}&size={}, userId={} с телом={}", state, from, size, itemsOwnerId, bookings);
        return bookings;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int bookingId
    ) {
        log.info("Пришел GET-запрос /bookings/{bookingId={}}, userId={} без тела", bookingId, userId);
        BookingDtoResponse dto = bookingMapper.bookingToDtoResponse(bookingService.findById(bookingId, userId));
        log.info("Ответ на GET-запрос /bookings/{bookingId={}}, userId={} с телом={}", bookingId, userId, dto);
        return dto;
    }

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestHeader("X-Sharer-User-Id") int bookerId,
            @RequestBody BookingDtoRequest dto
    ) {
        log.info("Пришел POST-запрос /booking, userId={} с телом={}", bookerId, dto);
        BookingDtoResponse savedDto = bookingMapper.bookingToDtoResponse(bookingService.create(dto, bookerId));
        log.info("Ответ на POST-запрос /booking, userId={} с телом={}", bookerId, savedDto);
        return savedDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse patchBooking(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int bookingId,
            @RequestParam(name = "approved") boolean isApproved
    ) {
        log.info("Пришел PATCH-запрос /booking/{bookingId={}}?approved={}, userId={} без тела", bookingId, isApproved, userId);
        BookingDtoResponse updatedDto = bookingMapper.bookingToDtoResponse(bookingService.update(bookingId, isApproved, userId));
        log.info("Ответ на PATCH-запрос /booking/{bookingId={}}?approved={}, userId={} с телом={}", bookingId, isApproved, userId, updatedDto);
        return updatedDto;
    }
}
