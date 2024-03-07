package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.LongItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "db.name=item_test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemTests {

    final UserService userService;

    final ItemService itemService;

    List<User> expectedOwners = new ArrayList<>();

    List<Item> expectedItems = new ArrayList<>();

    @BeforeEach
    void setup() {
        UserDtoRequest ownerDto1 = UserDtoRequest.builder()
                .name("Alex")
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
                .name("Кастрюля")
                .description("Роскошная")
                .available(Boolean.TRUE)
                .build();
        ItemDtoRequest itemDto2 = ItemDtoRequest.builder()
                .name("Шуруповерт")
                .description("Антиквариантный")
                .available(Boolean.TRUE)
                .build();
        ItemDtoRequest itemDto3 = ItemDtoRequest.builder()
                .name("Ложка")
                .description("Серебряная")
                .available(Boolean.TRUE)
                .build();
        Item item1 = itemService.create(itemDto1, owner1.getId());
        Item item2 = itemService.create(itemDto2, owner2.getId());
        Item item3 = itemService.create(itemDto3, owner1.getId());
        expectedItems.add(item1);
        expectedItems.add(item2);
        expectedItems.add(item3);
    }

    @Test
    void shouldFindLongDtosOfOwners() {
        Item item1 = expectedItems.get(0);
        Item item2 = expectedItems.get(1);
        Item item3 = expectedItems.get(2);
        List<LongItemDtoResponse> expectedDtosOfOwner1 = List.of(
                LongItemDtoResponse.builder()
                        .id(item1.getId())
                        .name(item1.getName())
                        .description(item1.getDescription())
                        .nextBooking(null)
                        .lastBooking(null)
                        .available(item1.getAvailable())
                        .comments(Collections.emptyList())
                        .build(),
                LongItemDtoResponse.builder()
                        .id(item3.getId())
                        .name(item3.getName())
                        .description(item3.getDescription())
                        .nextBooking(null)
                        .lastBooking(null)
                        .available(item3.getAvailable())
                        .comments(Collections.emptyList())
                        .build()
        );
        List<LongItemDtoResponse> expectedDtosOfOwner2 = Collections.singletonList(
                LongItemDtoResponse.builder()
                        .id(item2.getId())
                        .name(item2.getName())
                        .description(item2.getDescription())
                        .nextBooking(null)
                        .lastBooking(null)
                        .available(item2.getAvailable())
                        .comments(Collections.emptyList())
                        .build()
        );

        List<LongItemDtoResponse> actualDtosOfOwner1 = itemService.findLongItemDtosOfUser(
                expectedOwners.get(0).getId(), 0, 10
        );
        List<LongItemDtoResponse> actualDtosOfOwner2 = itemService.findLongItemDtosOfUser(
                expectedOwners.get(1).getId(), 0, 10
        );

        assertEquals(expectedDtosOfOwner1, actualDtosOfOwner1);
        assertEquals(expectedDtosOfOwner2, actualDtosOfOwner2);
    }

    @Test
    void shouldFindItemsContainingText() {
        String text1 = "КА";
        String text2 = "шуруп";
        List<Item> expectedItemsForText1 = List.of(
                expectedItems.get(0),
                expectedItems.get(2)
        );
        List<Item> expectedItemsForText2 = Collections.singletonList(expectedItems.get(1));

        List<Item> actualItemsForText1 = itemService.findItemsContainingText(text1, 0, 10);
        List<Item> actualItemsForText2 = itemService.findItemsContainingText(text2, 0, 10);

        assertEquals(expectedItemsForText1, actualItemsForText1);
        assertEquals(expectedItemsForText2, actualItemsForText2);
    }
}
