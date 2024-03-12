package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
        properties = "db.name=user_test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserTests {

    final UserService userService;

    List<User> expectedUsers = new ArrayList<>();

    @BeforeEach
    void setup() {
        UserDtoRequest dto1 = UserDtoRequest.builder()
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        UserDtoRequest dto2 = UserDtoRequest.builder()
                .name("John")
                .email("John@gmail.com")
                .build();
        User user1 = userService.create(dto1);
        User user2 = userService.create(dto2);
        expectedUsers.add(user1);
        expectedUsers.add(user2);
    }

    @Test
    void shouldFindAllAndDeleteThem() {
        List<User> expectedUsersAfterDelete = Collections.emptyList();

        List<User> actualUsersBeforeDelete = userService.findAll();
        actualUsersBeforeDelete.stream().map(User::getId).forEach(userService::delete);
        List<User> actualUsersAfterDelete = userService.findAll();

        assertEquals(expectedUsers, actualUsersBeforeDelete);
        assertEquals(expectedUsersAfterDelete, actualUsersAfterDelete);
    }

    @Test
    void shouldUpdateUser() {
        String expectedEmail = "NewAlex@gmail.com";
        UserDtoRequest dto = UserDtoRequest.builder()
                .email(expectedEmail)
                .build();
        User user1 = expectedUsers.get(0);
        User expectedUser = userService.findById(user1.getId());
        expectedUser.setEmail(expectedEmail);

        User actualUser = userService.update(user1.getId(), dto);
        String actualEmail = actualUser.getEmail();

        assertEquals(expectedUser, actualUser);
        assertEquals(expectedEmail, actualEmail);
    }

}
