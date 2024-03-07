package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.RequestBookingStates;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTests {

    @Mock
    BookingService mockBookingService;

    @Mock
    BookingMapper mockBookingMapper;

    @InjectMocks
    BookingController bookingController;

    final ObjectMapper mapper = new ObjectMapper();

    MockMvc mockMvc;

    BookingDtoRequest dtoRequest;

    BookingDtoResponse dtoResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
        mapper.registerModule(new JavaTimeModule());
        LocalDateTime now = LocalDateTime.now();
        dtoRequest = BookingDtoRequest.builder()
                .itemId(1)
                .start(now.plusHours(2))
                .end(now.plusHours(10))
                .build();
        dtoResponse = BookingDtoResponse.builder()
                .id(1)
                .booker(UserDtoResponse.builder().id(2).name("Alex").email("Alex@gmail.com").build())
                .item(ItemDtoResponse.builder().id(1).name("Кастрюля").description("Роскошная").requestId(1).available(Boolean.TRUE).build())
                .status(BookingStatus.WAITING)
                .start(now.plusHours(2))
                .end(now.plusHours(10))
                .build();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        lenient().when(mockBookingService.create(dtoRequest, 2))
                .thenReturn(new Booking());
        when(mockBookingMapper.bookingToDtoResponse(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(dtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(dtoResponse.getStart().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.end", is(dtoResponse.getEnd().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.booker.id", is(dtoResponse.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(dtoResponse.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(dtoResponse.getBooker().getEmail())))
                .andExpect(jsonPath("$.item.id", is(dtoResponse.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(dtoResponse.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(dtoResponse.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(dtoResponse.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.item.requestId", is(dtoResponse.getItem().getRequestId()), Integer.class));
    }

    @Test
    void shouldThrowIfDtoRequestIsInvalid() throws Exception {
        BookingDtoRequest invalidDtoRequest = BookingDtoRequest.builder()
                .start(null)
                .end(null)
                .itemId(1)
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(invalidDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetBookingsOfUser() throws Exception {
        when(mockBookingService.findBookingsOfUserByState(2, RequestBookingStates.ALL, 0, 10))
                .thenReturn(Collections.emptyList());
        when(mockBookingMapper.bookingsToDtoResponses(any()))
                .thenReturn(List.of(dtoResponse));

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(dtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", is(dtoResponse.getStart().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", is(dtoResponse.getEnd().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].booker.id", is(dtoResponse.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.name", is(dtoResponse.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", is(dtoResponse.getBooker().getEmail())))
                .andExpect(jsonPath("$[0].item.id", is(dtoResponse.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(dtoResponse.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(dtoResponse.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(dtoResponse.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].item.requestId", is(dtoResponse.getItem().getRequestId()), Integer.class));
    }

    @Test
    void shouldGetBookingsOfItemsOwner() throws Exception {
        lenient().when(mockBookingService.findBookingsOfItemsOwnerByState(1, RequestBookingStates.ALL, 0, 10))
                .thenReturn(Collections.emptyList());
        when(mockBookingMapper.bookingsToDtoResponses(any()))
                .thenReturn(List.of(dtoResponse));

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(dtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", is(dtoResponse.getStart().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].end", is(dtoResponse.getEnd().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].booker.id", is(dtoResponse.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.name", is(dtoResponse.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", is(dtoResponse.getBooker().getEmail())))
                .andExpect(jsonPath("$[0].item.id", is(dtoResponse.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(dtoResponse.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(dtoResponse.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(dtoResponse.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].item.requestId", is(dtoResponse.getItem().getRequestId()), Integer.class));
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(mockBookingService.findById(1, 1))
                .thenReturn(new Booking());
        when(mockBookingMapper.bookingToDtoResponse(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(dtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(dtoResponse.getStart().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.end", is(dtoResponse.getEnd().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.booker.id", is(dtoResponse.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(dtoResponse.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(dtoResponse.getBooker().getEmail())))
                .andExpect(jsonPath("$.item.id", is(dtoResponse.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(dtoResponse.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(dtoResponse.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(dtoResponse.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.item.requestId", is(dtoResponse.getItem().getRequestId()), Integer.class));
    }

    @Test
    void shouldPatchUpdateBooking() throws Exception {
        lenient().when(mockBookingService.update(1, true, 1))
                .thenReturn(new Booking());
        dtoResponse.setStatus(BookingStatus.APPROVED);
        when(mockBookingMapper.bookingToDtoResponse(any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(dtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(dtoResponse.getStart().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.end", is(dtoResponse.getEnd().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.booker.id", is(dtoResponse.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(dtoResponse.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(dtoResponse.getBooker().getEmail())))
                .andExpect(jsonPath("$.item.id", is(dtoResponse.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(dtoResponse.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(dtoResponse.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(dtoResponse.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.item.requestId", is(dtoResponse.getItem().getRequestId()), Integer.class));
    }

}
