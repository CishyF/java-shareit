package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.util.CommentMapperImpl;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapperImpl;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTests {

    @Mock
    ItemService mockItemService;

    @Mock
    ItemMapperImpl mockItemMapper;

    @Mock
    UserService mockUserService;

    @Mock
    CommentMapperImpl mockCommentMapper;

    @InjectMocks
    ItemController itemController;

    final ObjectMapper mapper = new ObjectMapper();

    MockMvc mockMvc;

    CommentDtoRequest commentDtoRequest;

    ItemDtoRequest itemDtoRequest;

    CommentDtoResponse commentDtoResponse;

    ItemDtoResponse itemDtoResponse;

    LongItemDtoResponse longItemDtoResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
        commentDtoRequest = CommentDtoRequest.builder()
                .text("Действительно роскошная!")
                .build();
        itemDtoRequest = ItemDtoRequest.builder()
                .requestId(1)
                .name("Кастрюля")
                .description("Роскошная")
                .available(Boolean.TRUE)
                .build();
        commentDtoResponse = CommentDtoResponse.builder()
                .id(1)
                .text("Действительно роскошная!")
                .authorName("Alex")
                .created(LocalDateTime.now())
                .build();
        itemDtoResponse = ItemDtoResponse.builder()
                .id(1)
                .requestId(1)
                .name("Кастрюля")
                .description("Роскошная")
                .available(Boolean.TRUE)
                .build();
        longItemDtoResponse = LongItemDtoResponse.builder()
                .id(itemDtoResponse.getId())
                .name(itemDtoResponse.getName())
                .description(itemDtoResponse.getDescription())
                .available(itemDtoResponse.getAvailable())
                .nextBooking(null)
                .lastBooking(null)
                .comments(List.of(commentDtoResponse))
                .build();
    }

    @Test
    void shouldCreateItem() throws Exception {
        Item item = Item.builder()
                .id(itemDtoResponse.getId())
                .name(itemDtoResponse.getName())
                .description(itemDtoResponse.getDescription())
                .owner(User.builder().id(itemDtoResponse.getId()).build())
                .available(itemDtoResponse.getAvailable())
                .request(ItemRequest.builder().id(itemDtoResponse.getId()).build())
                .build();
        lenient().when(mockItemService.create(itemDtoRequest, 1))
                .thenReturn(item);
        when(mockItemMapper.itemToDtoResponse(item))
                .thenCallRealMethod();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoResponse.getRequestId())))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable()), Boolean.class));
    }

    @Test
    void shouldThrowIfItemDtoRequestIsInvalid() throws Exception {
        ItemDtoRequest invalidDtoRequest = ItemDtoRequest.builder()
                .requestId(null)
                .name("  ")
                .description("  ")
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(invalidDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateComment() throws Exception {
        Comment comment = Comment.builder()
                        .id(1)
                        .text("Действительно роскошная!")
                        .author(User.builder().name("Alex").build())
                        .createdAt(commentDtoResponse.getCreated())
                        .build();
        when(mockItemService.createComment(commentDtoRequest, 1, 2))
                .thenReturn(comment);
        when(mockCommentMapper.commentToDtoResponse(comment))
                .thenCallRealMethod();

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDtoResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoResponse.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDtoResponse.getCreated().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")))));
    }

    @Test
    void shouldThrowIfCommentDtoRequestIsInvalid() throws Exception {
        CommentDtoRequest invalidDtoRequest = CommentDtoRequest.builder()
                .text("  ")
                .build();

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(invalidDtoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetItemsOfUserWithCustomSize() throws Exception {
        when(mockItemService.findLongItemDtosOfUser(1, 0, 1))
                .thenReturn(List.of(longItemDtoResponse));

        mockMvc.perform(get("/items?size=1")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(longItemDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(longItemDtoResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(longItemDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(longItemDtoResponse.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].lastBooking", is(longItemDtoResponse.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(longItemDtoResponse.getNextBooking())))
                .andExpect(jsonPath("$[0].comments[0].id", is(commentDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(commentDtoResponse.getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(commentDtoResponse.getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created", is(commentDtoResponse.getCreated().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")))));
    }

    @Test
    void shouldGetItemsContainingText() throws Exception {
        lenient().when(mockItemService.findItemsContainingText("Кастрюля", 0, 10))
                .thenReturn(Collections.emptyList());
        when(mockItemMapper.itemsToDtoResponses(any()))
                .thenReturn(List.of(itemDtoResponse));

        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoResponse.getRequestId())))
                .andExpect(jsonPath("$[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoResponse.getAvailable()), Boolean.class));
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(mockItemService.findLongItemDtoById(1, 1))
                .thenReturn(longItemDtoResponse);

        mockMvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(longItemDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(longItemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(longItemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(longItemDtoResponse.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.lastBooking", is(longItemDtoResponse.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(longItemDtoResponse.getNextBooking())))
                .andExpect(jsonPath("$.comments[0].id", is(commentDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.comments[0].text", is(commentDtoResponse.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(commentDtoResponse.getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(commentDtoResponse.getCreated().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")))));
    }

    @Test
    void shouldPatchUpdateItem() throws Exception {
        lenient().when(mockItemService.update(itemDtoRequest, 1, 1))
                .thenReturn(new Item());
        when(mockItemMapper.itemToDtoResponse(any()))
                .thenReturn(itemDtoResponse);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoResponse.getRequestId())))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable()), Boolean.class));
    }

    @Test
    void shouldThrowIfUserThatPatchUpdatingIsNotOwner() throws Exception {
        when(mockItemService.update(itemDtoRequest, 1, 2))
                .thenThrow(new AccessException("error message"));

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isForbidden());
    }

}
