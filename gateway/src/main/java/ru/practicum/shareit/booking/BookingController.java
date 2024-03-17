package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") int userId,
			@RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
			@Positive @RequestParam(name = "size", required = false, defaultValue = "10") int size
	) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Пришел GET-запрос /bookings?state={}&from={}&size={}, userId={} без тела", state, from, size, userId);
		ResponseEntity<Object> response = bookingClient.getBookingsOfUser(userId, state, from, size);
		log.info("Ответ на GET-запрос /bookings?state={}&from={}&size={}, userId={} с телом={}", state, from, size, userId, response);
		return response;
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOfItemsOwner(@RequestHeader("X-Sharer-User-Id") int userId,
			@RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
			@Positive @RequestParam(name = "size", required = false, defaultValue = "10") int size
	) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Пришел GET-запрос /bookings/owner?state={}&from={}&size={}, userId={} без тела", state, from, size, userId);
		ResponseEntity<Object> response = bookingClient.getBookingsOfItemsOwner(userId, state, from, size);
		log.info("Ответ на GET-запрос /bookings/owner?state={}&from={}&size={}, userId={} с телом={}", state, from, size, userId, response);
		return response;
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
			@PathVariable int bookingId) {
		log.info("Пришел GET-запрос /bookings/{bookingId={}}, userId={} без тела", bookingId, userId);
		ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);
		log.info("Ответ на GET-запрос /bookings/{bookingId={}}, userId={} с телом={}", bookingId, userId, response);
		return response;
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
			@RequestBody @Valid CreateBookingDto dto) {
		log.info("Пришел POST-запрос /booking, userId={} с телом={}", userId, dto);
		ResponseEntity<Object> response = bookingClient.createBooking(userId, dto);
		log.info("Ответ на POST-запрос /booking, userId={} с телом={}", userId, response);
		return response;
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> patchBooking(@RequestHeader("X-Sharer-User-Id") int userId,
		    @PathVariable int bookingId,
		    @RequestParam(name = "approved", required = false, defaultValue = "false") boolean isApproved
	) {
		log.info("Пришел PATCH-запрос /booking/{bookingId={}}?approved={}, userId={} без тела", bookingId, isApproved, userId);
		ResponseEntity<Object> response = bookingClient.patchBooking(userId, bookingId, isApproved);
		log.info("Ответ на PATCH-запрос /booking/{bookingId={}}?approved={}, userId={} с телом={}", bookingId, isApproved, userId, response);
		return response;
	}

}
