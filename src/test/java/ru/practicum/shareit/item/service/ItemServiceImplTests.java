package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.exception.EntityIsNotAvailableException;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.util.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.item.util.ItemPatchUpdater;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTests {

    @Mock
    UserService mockUserService;

    @Mock
    ItemRequestService mockRequestService;

    @Mock
    ItemRepository mockItemRepo;

    @Mock
    BookingRepository mockBookingRepo;

    @Mock
    CommentRepository mockCommentRepo;

    @Mock
    ItemMapper mockItemMapper;

    @Mock
    BookingMapper mockBookingMapper;

    @Mock
    CommentMapper mockCommentMapper;

    @Mock
    ItemPatchUpdater mockItemPatchUpdater;

    @InjectMocks
    ItemServiceImpl itemService;

    User owner;

    Item expectedItem;

    @BeforeEach
    void setup() {
        owner = User.builder()
                .id(1)
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        expectedItem = Item.builder()
                .id(1)
                .name("Кастрюля")
                .description("Роскошная")
                .owner(owner)
                .available(Boolean.TRUE)
                .build();
    }

    @Test
    void shouldCreateItemWithoutRequest() {
        when(mockUserService.findById(1))
                .thenReturn(owner);
        ItemDtoRequest dto = ItemDtoRequest.builder()
                .name("Кастрюля")
                .description("Роскошная")
                .requestId(null)
                .available(Boolean.TRUE)
                .build();
        when(mockItemMapper.dtoRequestToItem(dto, owner, null))
                .thenReturn(expectedItem);
        when(mockItemRepo.save(expectedItem))
                .thenReturn(expectedItem);

        Item actualItem = itemService.create(dto, 1);

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockItemMapper, times(1))
                .dtoRequestToItem(dto, owner, null);
        verify(mockItemRepo, times(1))
                .save(expectedItem);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    void shouldCreateItemWithRequest() {
        when(mockUserService.findById(1))
                .thenReturn(owner);
        ItemRequest request = ItemRequest.builder()
                .id(1)
                .items(new ArrayList<>())
                .build();
        when(mockRequestService.findById(1))
                .thenReturn(request);
        ItemDtoRequest dto = ItemDtoRequest.builder()
                .name("Кастрюля")
                .description("Роскошная")
                .requestId(1)
                .available(Boolean.TRUE)
                .build();
        when(mockItemMapper.dtoRequestToItem(dto, owner, request))
                .thenReturn(expectedItem);
        when(mockItemRepo.save(expectedItem))
                .thenReturn(expectedItem);

        Item actualItem = itemService.create(dto, 1);

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockRequestService, times(1))
                .findById(1);
        verify(mockItemMapper, times(1))
                .dtoRequestToItem(dto, owner, request);
        verify(mockItemRepo, times(1))
                .save(expectedItem);
        assertEquals(expectedItem, actualItem);
        assertEquals(expectedItem, request.getItems().get(0));
    }

    @Test
    void shouldFindItemsOfOwner() {
        when(mockUserService.findById(1))
                .thenReturn(owner);
        List<Item> expectedItems = Collections.singletonList(expectedItem);
        when(mockItemRepo.findItemsByOwnerOrderById(owner))
                .thenReturn(expectedItems);

        List<Item> actualItems = itemService.findItemsOfUser(1);

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockItemRepo, times(1))
                .findItemsByOwnerOrderById(owner);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    void shouldFindItemById() {
        when(mockItemRepo.findById(1))
                .thenReturn(Optional.of(expectedItem));

        Item actualItem = itemService.findById(1);

        verify(mockItemRepo, times(1))
                .findById(1);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    void shouldThrowIfItemWithIdDoesNotExist() {
        when(mockItemRepo.findById(2))
                .thenReturn(Optional.empty());
        String expectedMessage = "Попытка получить несуществующий предмет";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> itemService.findById(2)
        ).getMessage();

        verify(mockItemRepo, times(1))
                .findById(2);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindLongItemDtosOfUser() {
        when(mockUserService.findById(1))
                .thenReturn(owner);
        when(mockItemRepo.findById(1))
                .thenReturn(Optional.of(expectedItem));
        int from = 0;
        int size = 1;
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        when(mockItemRepo.findItemsByOwnerOrderById(owner, pageRequest))
                .thenReturn(new PageImpl<>(Collections.singletonList(expectedItem)));
        when(mockCommentRepo.findAllByItem(expectedItem))
                .thenReturn(Collections.emptyList());
        when(mockCommentMapper.commentsToDtoResponses(Collections.emptyList()))
                .thenReturn(Collections.emptyList());
        PageRequest pageRequestForBookings = PageRequest.of(0, 1);
        when(mockBookingRepo.findLastBookingByItem(expectedItem, pageRequestForBookings))
                .thenReturn(Page.empty());
        when(mockBookingRepo.findNextBookingByItem(expectedItem, pageRequestForBookings))
                .thenReturn(Page.empty());
        when(mockBookingMapper.bookingToShortDtoResponse(null))
                .thenReturn(null);
        LongItemDtoResponse expectedLongItemDto = LongItemDtoResponse.builder()
                .id(1)
                .name("Кастрюля")
                .description("Роскошная")
                .available(Boolean.TRUE)
                .comments(Collections.emptyList())
                .lastBooking(null)
                .nextBooking(null)
                .build();
        when(mockItemMapper.itemToLongDtoResponse(expectedItem, null, null, Collections.emptyList()))
                .thenReturn(expectedLongItemDto);
        List<LongItemDtoResponse> expectedLongItemDtos = Collections.singletonList(expectedLongItemDto);

        List<LongItemDtoResponse> actualLongItemDtos = itemService.findLongItemDtosOfUser(1, 0, 1);

        verify(mockUserService, times(1))
                .findById(1);
        verify(mockItemRepo, times(1))
                .findById(1);
        verify(mockCommentRepo, times(1))
                .findAllByItem(expectedItem);
        verify(mockCommentMapper, times(1))
                .commentsToDtoResponses(Collections.emptyList());
        verify(mockBookingRepo, times(1))
                .findLastBookingByItem(expectedItem, pageRequestForBookings);
        verify(mockBookingRepo, times(1))
                .findNextBookingByItem(expectedItem, pageRequestForBookings);
        verify(mockBookingMapper, times(2))
                .bookingToShortDtoResponse(null);
        verify(mockItemMapper, times(1))
                .itemToLongDtoResponse(expectedItem, null, null, Collections.emptyList());
        assertEquals(expectedLongItemDtos, actualLongItemDtos);
    }

    @Test
    void shouldFindItemsContainingText() {
        String text = "Кастрюля";
        int from = 0;
        int size = 1;
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Item> expectedItems = Collections.singletonList(expectedItem);
        when(mockItemRepo.findItemsContainingTextAndAvailable(text, pageRequest))
                .thenReturn(new PageImpl<>(expectedItems));

        List<Item> actualItems = itemService.findItemsContainingText(text, from, size);

        verify(mockItemRepo, times(1))
                .findItemsContainingTextAndAvailable(text, pageRequest);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    void shouldReturnEmptyListIfTextIsNullOrBlank() {
        List<Item> expectedItems = Collections.emptyList();

        List<Item> actualItemsWithNullableText = itemService.findItemsContainingText(null, 0, 1);
        List<Item> actualItemsWithBlankText = itemService.findItemsContainingText("  ", 0, 1);

        assertEquals(expectedItems, actualItemsWithNullableText);
        assertEquals(expectedItems, actualItemsWithBlankText);
    }

    @Test
    void shouldUpdateItem() {
        when(mockItemRepo.findById(1))
                .thenReturn(Optional.of(expectedItem));
        ItemDtoRequest dto = new ItemDtoRequest();
        when(mockItemRepo.save(expectedItem))
                .thenReturn(expectedItem);

        Item actualItem = itemService.update(dto, 1, 1);

        verify(mockItemRepo, times(1))
                .findById(1);
        verify(mockItemPatchUpdater, times(1))
                .updateItem(expectedItem, dto);
        verify(mockItemRepo, times(1))
                .save(expectedItem);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    void shouldThrowWhenUpdateItemThatDoesNotExist() {
        when(mockItemRepo.findById(2))
                .thenReturn(Optional.empty());
        ItemDtoRequest dto = new ItemDtoRequest();
        String expectedMessage = "Попытка обновить несуществующий предмет";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> itemService.update(dto, 2, 1)
        ).getMessage();

        verify(mockItemRepo, times(1))
                .findById(2);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowWhenUpdateByNotOwner() {
        when(mockItemRepo.findById(1))
                .thenReturn(Optional.of(expectedItem));
        ItemDtoRequest dto = new ItemDtoRequest();
        String expectedMessage = "Попытка обновить предмет, принадлежащий другому пользователю";

        String actualMessage = assertThrows(
                AccessException.class,
                () -> itemService.update(dto, 1, 2)
        ).getMessage();

        verify(mockItemRepo, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldCreateComment() {
        User author = User.builder()
                .id(2)
                .name("Hater")
                .build();
        when(mockUserService.findById(2))
                .thenReturn(author);
        when(mockItemRepo.findById(1))
                .thenReturn(Optional.of(expectedItem));
        Booking bookingOfAuthor = Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .item(expectedItem)
                .build();
        List<Booking> bookingsOfAuthor = Collections.singletonList(bookingOfAuthor);
        when(mockBookingRepo.findBookingsByBooker(author))
                .thenReturn(bookingsOfAuthor);
        Comment expectedComment = Comment.builder()
                .id(1)
                .text("Dislike, unsub")
                .author(author)
                .build();
        CommentDtoRequest dto = new CommentDtoRequest();
        when(mockCommentMapper.dtoRequestToComment(dto, expectedItem, author))
                .thenReturn(expectedComment);
        when(mockCommentRepo.save(expectedComment))
                .thenReturn(expectedComment);

        Comment actualComment = itemService.createComment(dto, 1, 2);

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockItemRepo, times(1))
                .findById(1);
        verify(mockBookingRepo, times(1))
                .findBookingsByBooker(author);
        verify(mockCommentMapper, times(1))
                .dtoRequestToComment(dto, expectedItem, author);
        verify(mockCommentRepo, times(1))
                .save(expectedComment);
        assertEquals(expectedComment, actualComment);
    }

    @Test
    void shouldThrowIfAuthorDidNotBookedTheItem() {
        User author = User.builder()
                .id(2)
                .name("Hater")
                .build();
        when(mockUserService.findById(2))
                .thenReturn(author);
        when(mockItemRepo.findById(1))
                .thenReturn(Optional.of(expectedItem));
        when(mockBookingRepo.findBookingsByBooker(author))
                .thenReturn(Collections.emptyList());
        CommentDtoRequest dto = new CommentDtoRequest();
        String expectedMessage = "У данного пользователя нет прав для оставления комментария у этой вещи";

        String actualMessage = assertThrows(
                EntityIsNotAvailableException.class,
                () -> itemService.createComment(dto, 1, 2)
        ).getMessage();

        verify(mockUserService, times(1))
                .findById(2);
        verify(mockItemRepo, times(1))
                .findById(1);
        verify(mockBookingRepo, times(1))
                .findBookingsByBooker(author);
        assertEquals(expectedMessage, actualMessage);
    }

}
