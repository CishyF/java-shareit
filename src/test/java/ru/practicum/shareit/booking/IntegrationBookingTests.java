package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.RequestBookingStates;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "db.name=booking_test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationBookingTests {

    final ItemService itemService;

    final UserService userService;

    final BookingService bookingService;

    List<Item> expectedItems = new ArrayList<>();

    List<User> expectedOwners = new ArrayList<>();

    List<User> expectedBookers = new ArrayList<>();

    List<Booking> expectedBookings = new ArrayList<>();

    @BeforeEach
    void setup() {
        UserDtoRequest ownerDto1 = UserDtoRequest.builder()
                .email("Alex")
                .email("Alex@gmail.com")
                .build();
        UserDtoRequest ownerDto2 = UserDtoRequest.builder()
                .name("John")
                .email("John@yandex.ru")
                .build();
        User owner1 = userService.create(ownerDto1);
        User owner2 = userService.create(ownerDto2);
        expectedOwners.add(owner1);
        expectedOwners.add(owner2);

        ItemDtoRequest itemDto1 = ItemDtoRequest.builder()
                .name("Шуруповерт")
                .description("Роскошный")
                .available(Boolean.TRUE)
                .build();
        ItemDtoRequest itemDto2 = ItemDtoRequest.builder()
                .name("Кастрюля")
                .description("Антиквариатная")
                .available(Boolean.TRUE)
                .build();
        ItemDtoRequest itemDto3 = ItemDtoRequest.builder()
                .name("Карандаш")
                .description("Заточенный")
                .available(Boolean.TRUE)
                .build();
        Item item1 = itemService.create(itemDto1, owner1.getId());
        Item item2 = itemService.create(itemDto2, owner2.getId());
        Item item3 = itemService.create(itemDto3, owner1.getId());
        expectedItems.add(item1);
        expectedItems.add(item2);
        expectedItems.add(item3);

        UserDtoRequest bookerDto1 = UserDtoRequest.builder()
                .name("Derrick")
                .email("Derrick@gmail.com")
                .build();
        UserDtoRequest bookerDto2 = UserDtoRequest.builder()
                .name("Gabe")
                .email("Gabe@yandex.ru")
                .build();
        User booker1 = userService.create(bookerDto1);
        User booker2 = userService.create(bookerDto2);
        expectedBookers.add(booker1);
        expectedBookers.add(booker2);

        // CURRENT
        BookingDtoRequest bookingDto1 = BookingDtoRequest.builder()
                .itemId(item1.getId())
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        // FUTURE
        BookingDtoRequest bookingDto2 = BookingDtoRequest.builder()
                .itemId(item2.getId())
                .start(LocalDateTime.now().plusHours(10))
                .end(LocalDateTime.now().plusDays(10))
                .build();
        // PAST
        BookingDtoRequest bookingDto3 = BookingDtoRequest.builder()
                .itemId(item3.getId())
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L))
                .build();
        Booking booking1 = bookingService.create(bookingDto1, booker1.getId());
        Booking booking2 = bookingService.create(bookingDto2, booker2.getId());
        Booking booking3 = bookingService.create(bookingDto3, booker1.getId());
        expectedBookings.add(booking1);
        expectedBookings.add(booking2);
        expectedBookings.add(booking3);
    }

    @Test
    void shouldFindAllBookingsOfOwners() {
        List<Booking> expectedBookingsOfOwner1 = Stream.of(
                expectedBookings.get(0),
                expectedBookings.get(2)
        ).sorted(Comparator.comparing(Booking::getStart).reversed()).collect(toList());
        List<Booking> expectedBookingsOfOwner2 = Collections.singletonList(expectedBookings.get(1));

        List<Booking> actualBookingsOfOwner1 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(0).getId(), RequestBookingStates.ALL, 0, 10
        );
        List<Booking> actualBookingsOfOwner2 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(1).getId(), RequestBookingStates.ALL, 0, 10
        );

        assertEquals(expectedBookingsOfOwner1, actualBookingsOfOwner1);
        assertEquals(expectedBookingsOfOwner2, actualBookingsOfOwner2);
    }

    @Test
    void shouldFindCurrentBookingsOfOwners() throws InterruptedException {
        List<Booking> expectedBookingsOfOwner1 = Collections.singletonList(expectedBookings.get(0));
        List<Booking> expectedBookingsOfOwner2 = Collections.emptyList();

        Thread.sleep(2000L);
        List<Booking> actualBookingsOfOwner1 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(0).getId(), RequestBookingStates.CURRENT, 0, 10
        );
        List<Booking> actualBookingsOfOwner2 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(1).getId(), RequestBookingStates.CURRENT, 0, 10
        );

        assertEquals(expectedBookingsOfOwner1, actualBookingsOfOwner1);
        assertEquals(expectedBookingsOfOwner2, actualBookingsOfOwner2);
    }

    @Test
    void shouldFindPastBookingsOfOwners() throws InterruptedException {
        List<Booking> expectedBookingsOfOwner1 = Collections.singletonList(expectedBookings.get(2));
        List<Booking> expectedBookingsOfOwner2 = Collections.emptyList();

        Thread.sleep(2000L);
        List<Booking> actualBookingsOfOwner1 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(0).getId(), RequestBookingStates.PAST, 0, 10
        );
        List<Booking> actualBookingsOfOwner2 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(1).getId(), RequestBookingStates.PAST, 0, 10
        );

        assertEquals(expectedBookingsOfOwner1, actualBookingsOfOwner1);
        assertEquals(expectedBookingsOfOwner2, actualBookingsOfOwner2);
    }

    @Test
    void shouldFindFutureBookingsOfOwners() throws InterruptedException {
        List<Booking> expectedBookingsOfOwner1 = Collections.emptyList();
        List<Booking> expectedBookingsOfOwner2 = Collections.singletonList(expectedBookings.get(1));

        Thread.sleep(1000L);
        List<Booking> actualBookingsOfOwner1 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(0).getId(), RequestBookingStates.FUTURE, 0, 10
        );
        List<Booking> actualBookingsOfOwner2 = bookingService.findBookingsOfItemsOwnerByState(
                expectedOwners.get(1).getId(), RequestBookingStates.FUTURE, 0, 10
        );

        assertEquals(expectedBookingsOfOwner1, actualBookingsOfOwner1);
        assertEquals(expectedBookingsOfOwner2, actualBookingsOfOwner2);
    }

    @Test
    void shouldFindAllBookingsOfBookers() {
        List<Booking> expectedBookingsOfBooker1 = Stream.of(
                expectedBookings.get(0),
                expectedBookings.get(2)
        ).sorted(Comparator.comparing(Booking::getStart).reversed()).collect(toList());
        List<Booking> expectedBookingsOfBooker2 = Collections.singletonList(expectedBookings.get(1));

        List<Booking> actualBookingsOfBooker1 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(0).getId(), RequestBookingStates.ALL, 0, 10
        );
        List<Booking> actualBookingsOfBooker2 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(1).getId(), RequestBookingStates.ALL, 0, 10
        );

        assertEquals(expectedBookingsOfBooker1, actualBookingsOfBooker1);
        assertEquals(expectedBookingsOfBooker2, actualBookingsOfBooker2);
    }

    @Test
    void shouldFindCurrentBookingsOfBookers() throws InterruptedException {
        List<Booking> expectedBookingsOfBooker1 = Collections.singletonList(expectedBookings.get(0));
        List<Booking> expectedBookingsOfBooker2 = Collections.emptyList();

        Thread.sleep(2000L);
        List<Booking> actualBookingsOfBooker1 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(0).getId(), RequestBookingStates.CURRENT, 0, 10
        );
        List<Booking> actualBookingsOfBooker2 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(1).getId(), RequestBookingStates.CURRENT, 0, 10
        );

        assertEquals(expectedBookingsOfBooker1, actualBookingsOfBooker1);
        assertEquals(expectedBookingsOfBooker2, actualBookingsOfBooker2);
    }

    @Test
    void shouldFindPastBookingsOfBookers() throws InterruptedException {
        List<Booking> expectedBookingsOfBooker1 = Collections.singletonList(expectedBookings.get(2));
        List<Booking> expectedBookingsOfBooker2 = Collections.emptyList();

        Thread.sleep(2000L);
        List<Booking> actualBookingsOfBooker1 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(0).getId(), RequestBookingStates.PAST, 0, 10
        );
        List<Booking> actualBookingsOfBooker2 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(1).getId(), RequestBookingStates.PAST, 0, 10
        );

        assertEquals(expectedBookingsOfBooker1, actualBookingsOfBooker1);
        assertEquals(expectedBookingsOfBooker2, actualBookingsOfBooker2);
    }

    @Test
    void shouldFindFutureBookingsOfBookers() throws InterruptedException {
        List<Booking> expectedBookingsOfBooker1 = Collections.emptyList();
        List<Booking> expectedBookingsOfBooker2 = Collections.singletonList(expectedBookings.get(1));

        Thread.sleep(1000L);
        List<Booking> actualBookingsOfBooker1 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(0).getId(), RequestBookingStates.FUTURE, 0, 10
        );
        List<Booking> actualBookingsOfBooker2 = bookingService.findBookingsOfUserByState(
                expectedBookers.get(1).getId(), RequestBookingStates.FUTURE, 0, 10
        );

        assertEquals(expectedBookingsOfBooker1, actualBookingsOfBooker1);
        assertEquals(expectedBookingsOfBooker2, actualBookingsOfBooker2);
    }

    @Test
    void shouldUpdateStatusWaitingToApproved() {
        Booking booking1 = expectedBookings.get(0);
        Booking expectedBooking = Booking.builder()
                .id(booking1.getId())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        Booking actualBooking = bookingService.update(
                booking1.getId(), true, expectedOwners.get(0).getId()
        );

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void shouldUpdateStatusWaitingToRejected() {
        Booking booking2 = expectedBookings.get(1);
        Booking expectedBooking = Booking.builder()
                .id(booking2.getId())
                .item(booking2.getItem())
                .booker(booking2.getBooker())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(BookingStatus.REJECTED)
                .build();

        Booking actualBooking = bookingService.update(
                booking2.getId(), false, expectedOwners.get(1).getId()
        );

        assertEquals(expectedBooking, actualBooking);
    }

}
