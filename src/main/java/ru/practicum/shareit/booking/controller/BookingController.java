package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.RequestBookingStates;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingMapper;

import javax.validation.Valid;
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
            @RequestParam(value = "state", required = false, defaultValue = "ALL") RequestBookingStates state
    ) {
        log.info("Пришел GET-запрос /bookings?state={} без тела", state);
        Collection<BookingDtoResponse> bookings = bookingMapper.bookingsToDtoResponses(
                bookingService.findBookingsOfUserByState(bookerId, state)
        );
        log.info("Ответ на GET-запрос /bookings?state={} с телом={}", state, bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getBookingsOfBookerItemsByState(
            @RequestHeader("X-Sharer-User-Id") int bookerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") RequestBookingStates state
    ) {
        log.info("Пришел GET-запрос /bookings/owner?state={} без тела", state);
        Collection<BookingDtoResponse> bookings = bookingMapper.bookingsToDtoResponses(
                bookingService.findBookingsOfBookerItemsByState(bookerId, state)
        );
        log.info("Ответ на GET-запрос /bookings/owner?state={} с телом={}", state, bookings);
        return bookings;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable("bookingId") int id
    ) {
        log.info("Пришел GET-запрос /bookings/{bookingId={}} без тела", id);
        BookingDtoResponse dto = bookingMapper.bookingToDtoResponse(bookingService.findById(id, userId));
        log.info("Ответ на GET-запрос /bookings/{bookingId={}} с телом={}", id, dto);
        return dto;
    }

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestHeader("X-Sharer-User-Id") int bookerId,
            @RequestBody @Valid BookingDtoRequest dto
    ) {
        log.info("Пришел POST-запрос /booking с телом={}", dto);
        BookingDtoResponse savedDto = bookingMapper.bookingToDtoResponse(bookingService.create(dto, bookerId));
        log.info("Ответ на POST-запрос /booking с телом={}", savedDto);
        return savedDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse patchBooking(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable("bookingId") int id,
            @RequestParam(name = "approved", required = false, defaultValue = "false") boolean isApproved
    ) {
        log.info("Пришел PATCH-запрос /booking/{bookingId={}}?approved={} без тела", id, isApproved);
        BookingDtoResponse updatedDto = bookingMapper.bookingToDtoResponse(bookingService.update(id, isApproved, userId));
        log.info("Ответ на PATCH-запрос /booking/{bookingId={}}?approved={} с телом={}", id, isApproved, updatedDto);
        return updatedDto;
    }
}
