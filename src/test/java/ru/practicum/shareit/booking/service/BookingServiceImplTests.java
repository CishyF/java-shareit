package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTests {

    @Mock
    UserService mockUserService;

    @Mock
    ItemService mockItemService;

    @Mock
    BookingRepository mockBookingRepo;

    @Mock
    BookingMapper mockBookingMapper;

    @InjectMocks
    BookingServiceImpl bookingService;

    User ownerOfItem;

    Item item;

    User booker;

    Booking expectedBooking;

    @BeforeEach
    void setup() {
        ownerOfItem = User.builder()
                .id(1)
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        item = Item.builder()
                .id(1)
                .name("Бутылка")
                .description("Описание")
                .available(true)
                .owner(ownerOfItem)
                .build();
        booker = User.builder()
                .id(2)
                .name("John")
                .email("John@yandex.ru")
                .build();
        expectedBooking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(7))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();
    }

    @Test
    void shouldCreateBooking() {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                        .itemId(1)
                        .build();
        when(mockUserService.findById(2))
                .thenReturn(booker);
        when(mockItemService.findById(1))
                .thenReturn(item);
        when(mockBookingMapper.dtoRequestToBooking(dto, item, booker))
                .thenReturn(expectedBooking);
        when(mockBookingRepo.save(expectedBooking))
                .thenReturn(expectedBooking);

        Booking actualBooking = bookingService.create(dto, 2);

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockItemService, times(1))
                .findById(1);
        verify(mockBookingMapper, times(1))
                .dtoRequestToBooking(dto, item, booker);
        verify(mockBookingRepo, times(1))
                .save(expectedBooking);
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void shouldThrowIfDtoItemIdIsNull() {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(null)
                .build();
        when(mockUserService.findById(2))
                .thenReturn(booker);
        String expectedMessage = "Попытка получить несуществующий предмет";
        when(mockItemService.findById(-1))
                .thenThrow(new EntityDoesNotExistException(expectedMessage));

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> bookingService.create(dto, 2)
        ).getMessage();

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockItemService, times(1))
                .findById(-1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowIfItemIsNotAvailable() {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(1)
                .build();
        item.setAvailable(Boolean.FALSE);
        when(mockUserService.findById(2))
                .thenReturn(booker);
        when(mockItemService.findById(1))
                .thenReturn(item);
        when(mockBookingMapper.dtoRequestToBooking(dto, item, booker))
                .thenReturn(expectedBooking);
        String expectedMessage = "Аренда с этим предметом уже существует";

        String actualMessage = assertThrows(
                EntityIsNotAvailableException.class,
                () -> bookingService.create(dto, 2)
        ).getMessage();

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockItemService, times(1))
                .findById(1);
        verify(mockBookingMapper, times(1))
                .dtoRequestToBooking(dto, item, booker);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowIfItemOwnerIsBooker() {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(1)
                .build();
        when(mockUserService.findById(1))
                .thenReturn(ownerOfItem);
        when(mockItemService.findById(1))
                .thenReturn(item);
        when(mockBookingMapper.dtoRequestToBooking(dto, item, ownerOfItem))
                .thenReturn(expectedBooking);
        String expectedMessage = "Попытка арендовать свою вещь";

        String actualMessage = assertThrows(
                BookingByOwnerOfItemException.class,
                () -> bookingService.create(dto, 1)
        ).getMessage();

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockItemService, times(1))
                .findById(1);
        verify(mockBookingMapper, times(1))
                .dtoRequestToBooking(dto, item, ownerOfItem);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindBookingsByItemInIfStateIsAll() {
        List<Item> items = Collections.singletonList(item);
        when(mockItemService.findItemsOfUser(1))
                .thenReturn(items);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findBookingsByItemIn(items, pageRequest))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfItemsOwnerByState(1, RequestBookingStates.ALL, from, size);

        verify(mockItemService, times(1))
                .findItemsOfUser(1);
        verify(mockBookingRepo, times(1))
                .findBookingsByItemIn(items, pageRequest);
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindCurrentBookingsByItemInIfStateIsCurrent() {
        List<Item> items = Collections.singletonList(item);
        when(mockItemService.findItemsOfUser(1))
                .thenReturn(items);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findCurrentBookingsByItemIn(items, pageRequest))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfItemsOwnerByState(1, RequestBookingStates.CURRENT, from, size);

        verify(mockItemService, times(1))
                .findItemsOfUser(1);
        verify(mockBookingRepo, times(1))
                .findCurrentBookingsByItemIn(items, pageRequest);
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindNothingByItemInIfStateIsPast() {
        List<Item> items = Collections.singletonList(item);
        when(mockItemService.findItemsOfUser(1))
                .thenReturn(items);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.emptyList();
        when(mockBookingRepo.findBookingsByItemInAndEndIsBefore(eq(items), any(LocalDateTime.class), eq(pageRequest)))
                .thenReturn(Page.empty());

        List<Booking> actualBookings = bookingService.findBookingsOfItemsOwnerByState(1, RequestBookingStates.PAST, from, size);

        verify(mockItemService, times(1))
                .findItemsOfUser(1);
        verify(mockBookingRepo, times(1))
                .findBookingsByItemInAndEndIsBefore(eq(items), any(LocalDateTime.class), eq(pageRequest));
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindBookingsByItemInAndStartIsAfterIfStateIsFuture() {
        List<Item> items = Collections.singletonList(item);
        when(mockItemService.findItemsOfUser(1))
                .thenReturn(items);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findBookingsByItemInAndStartIsAfter(eq(items), any(LocalDateTime.class), eq(pageRequest)))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfItemsOwnerByState(1, RequestBookingStates.FUTURE, from, size);

        verify(mockItemService, times(1))
                .findItemsOfUser(1);
        verify(mockBookingRepo, times(1))
                .findBookingsByItemInAndStartIsAfter(eq(items), any(LocalDateTime.class), eq(pageRequest));
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindBookingsByItemInAndStatusIfStateIsWaiting() {
        List<Item> items = Collections.singletonList(item);
        when(mockItemService.findItemsOfUser(1))
                .thenReturn(items);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findBookingsByItemInAndStatus(items, status, pageRequest))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfItemsOwnerByState(1, RequestBookingStates.WAITING, from, size);

        verify(mockItemService, times(1))
                .findItemsOfUser(1);
        verify(mockBookingRepo, times(1))
                .findBookingsByItemInAndStatus(items, status, pageRequest);
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindBookingsByBookerIfStateIsAll() {
        when(mockUserService.findById(2))
                .thenReturn(booker);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findBookingsByBooker(booker, pageRequest))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfUserByState(2, RequestBookingStates.ALL, from, size);

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockBookingRepo, times(1))
                .findBookingsByBooker(booker, pageRequest);
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindCurrentBookingsByBookerIfStateIsCurrent() {
        when(mockUserService.findById(2))
                .thenReturn(booker);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findCurrentBookingsByBooker(booker, pageRequest))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfUserByState(2, RequestBookingStates.CURRENT, from, size);

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockBookingRepo, times(1))
                .findCurrentBookingsByBooker(booker, pageRequest);
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindNothingByBookerIfStateIsPast() {
        when(mockUserService.findById(2))
                .thenReturn(booker);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.emptyList();
        when(mockBookingRepo.findBookingsByBookerAndEndIsBefore(eq(booker), any(LocalDateTime.class), eq(pageRequest)))
                .thenReturn(Page.empty());

        List<Booking> actualBookings = bookingService.findBookingsOfUserByState(2, RequestBookingStates.PAST, from, size);

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockBookingRepo, times(1))
                .findBookingsByBookerAndEndIsBefore(eq(booker), any(LocalDateTime.class), eq(pageRequest));
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindBookingsByBookerAndStartIsAfterIfStateIsFuture() {
        when(mockUserService.findById(2))
                .thenReturn(booker);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findBookingsByBookerAndStartIsAfter(eq(booker), any(LocalDateTime.class), eq(pageRequest)))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfUserByState(2, RequestBookingStates.FUTURE, from, size);

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockBookingRepo, times(1))
                .findBookingsByBookerAndStartIsAfter(eq(booker), any(LocalDateTime.class), eq(pageRequest));
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindBookingsByBookerAndStatusIfStateIsWaiting() {
        when(mockUserService.findById(2))
                .thenReturn(booker);
        int from = 0;
        int size = 1;
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        when(mockBookingRepo.findBookingsByBookerAndStatus(booker, status, pageRequest))
                .thenReturn(new PageImpl<>(expectedBookings));

        List<Booking> actualBookings = bookingService.findBookingsOfUserByState(2, RequestBookingStates.WAITING, from, size);

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockBookingRepo, times(1))
                .findBookingsByBookerAndStatus(booker, status, pageRequest);
        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void shouldFindBookingById() {
        when(mockBookingRepo.findById(1))
                .thenReturn(Optional.of(expectedBooking));

        Booking actualBooking = bookingService.findById(1, 1);

        verify(mockBookingRepo, times(1))
                .findById(1);
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void shouldThrowIfBookingFindingByNotOwnerAndBooker() {
        when(mockBookingRepo.findById(1))
                .thenReturn(Optional.of(expectedBooking));
        String expectedMessage = "Данной аренды нет у такого пользователя";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> bookingService.findById(1, 3)
        ).getMessage();

        verify(mockBookingRepo, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldApproveBooking() {
        when(mockBookingRepo.findById(1))
                .thenReturn(Optional.of(expectedBooking));
        when(mockBookingRepo.save(expectedBooking))
                .thenReturn(expectedBooking);

        Booking actualBooking = bookingService.update(1, true, 1);

        verify(mockBookingRepo, times(1))
                .findById(1);
        verify(mockBookingRepo, times(1))
                .save(expectedBooking);
        assertEquals(expectedBooking, actualBooking);
        assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());
    }

    @Test
    void shouldRejectBooking() {
        when(mockBookingRepo.findById(1))
                .thenReturn(Optional.of(expectedBooking));
        when(mockBookingRepo.save(expectedBooking))
                .thenReturn(expectedBooking);

        Booking actualBooking = bookingService.update(1, false, 1);

        verify(mockBookingRepo, times(1))
                .findById(1);
        verify(mockBookingRepo, times(1))
                .save(expectedBooking);
        assertEquals(expectedBooking, actualBooking);
        assertEquals(BookingStatus.REJECTED, actualBooking.getStatus());
    }

    @Test
    void shouldThrowIfUserThatUpdatingIsBooker() {
        when(mockBookingRepo.findById(1))
                .thenReturn(Optional.of(expectedBooking));
        String expectedMessage = "Данной аренды нет у такого пользователя";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> bookingService.update(1, true, 2)
        ).getMessage();

        verify(mockBookingRepo, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowIfUserThatUpdatingIsNotOwnerAndBooker() {
        when(mockBookingRepo.findById(1))
                .thenReturn(Optional.of(expectedBooking));
        String expectedMessage = "Аренда может обновляться только владельцем вещи";

        String actualMessage = assertThrows(
                EntityIsNotAvailableException.class,
                () -> bookingService.update(1, true, 3)
        ).getMessage();

        verify(mockBookingRepo, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowIfBookingStatusIsNotWaiting() {
        expectedBooking.setStatus(BookingStatus.REJECTED);
        when(mockBookingRepo.findById(1))
                .thenReturn(Optional.of(expectedBooking));
        String expectedMessage = "Попытка обновить ранее установленный статус";

        String actualMessage = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.update(1, true, 1)
        ).getMessage();

        verify(mockBookingRepo, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

}
