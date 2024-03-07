package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.util.UserMapper;
import ru.practicum.shareit.user.util.UserPatchUpdater;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class
UserServiceImplTests {

    @Mock
    UserRepository mockUserRepo;

    @Mock
    UserMapper mockUserMapper;

    @Mock
    UserPatchUpdater mockUserPatchUpdater;

    @InjectMocks
    UserServiceImpl userService;

    User expectedUser;

    @BeforeEach
    void setup() {
        expectedUser = User.builder()
                .id(1)
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
    }

    @Test
    void shouldCreateUser() {
        when(mockUserRepo.save(any(User.class)))
                .thenReturn(expectedUser);
        UserDtoRequest dto = UserDtoRequest.builder()
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        when(mockUserMapper.dtoRequestToUser(dto))
                .thenReturn(expectedUser);

        User actualUser = userService.create(dto);

        verify(mockUserRepo, times(1))
                .save(any(User.class));
        verify(mockUserMapper, times(1))
                .dtoRequestToUser(dto);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void shouldFindUsers() {
        List<User> expectedUsers = Collections.singletonList(expectedUser);
        when(mockUserRepo.findAll())
                .thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAll();

        verify(mockUserRepo, times(1))
                .findAll();
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void shouldFindUserById() {
        when(mockUserRepo.findById(1))
                .thenReturn(Optional.of(expectedUser));

        User actualUser = userService.findById(1);

        verify(mockUserRepo, times(1))
                .findById(1);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void shouldThrowIfUserWithIdDoesNotExist() {
        when(mockUserRepo.findById(2))
                .thenReturn(Optional.empty());
        String expectedMessage = "Попытка получения несуществующего пользователя";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> userService.findById(2)
        ).getMessage();

        verify(mockUserRepo, times(1))
                .findById(2);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldUpdateUser() {
        when(mockUserRepo.findById(1))
                .thenReturn(Optional.of(expectedUser));
        UserDtoRequest dto = UserDtoRequest.builder()
                .name("Alex")
                .email("Alex@gmail.com")
                .build();
        when(mockUserRepo.save(expectedUser))
                .thenReturn(expectedUser);

        User actualUser = userService.update(1, dto);

        verify(mockUserRepo, times(1))
                .findById(1);
        verify(mockUserPatchUpdater, times(1))
                .updateUser(expectedUser, dto);
        verify(mockUserRepo, times(1))
                .save(expectedUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void shouldThrowIfEmailOfDtoIsDuplicated() {
        when(mockUserRepo.findById(1))
                .thenReturn(Optional.of(expectedUser));
        UserDtoRequest dto = UserDtoRequest.builder()
                .name("Alex")
                .email("Duplicated@gmail.com")
                .build();
        when(mockUserRepo.countByEmailEquals("Duplicated@gmail.com"))
                .thenReturn(1);
        String expectedMessage = "Попытка присвоить пользователю уже использованную почту";

        String actualMessage = assertThrows(
                EntityAlreadyExistsException.class,
                () -> userService.update(1, dto)
        ).getMessage();

        verify(mockUserRepo, times(1))
                .findById(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowIfUserForUpdateDoesNotExist() {
        when(mockUserRepo.findById(2))
                .thenReturn(Optional.empty());
        String expectedMessage = "Попытка обновить несуществующего пользователя";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> userService.update(2, new UserDtoRequest())
        ).getMessage();

        verify(mockUserRepo, times(1))
                .findById(2);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldInvokeUserRepoDeleteMethodIfUserExist() {
        when(mockUserRepo.findById(1))
                .thenReturn(Optional.of(expectedUser));

        userService.delete(1);

        verify(mockUserRepo, times(1))
                .findById(1);
        verify(mockUserRepo, times(1))
                .delete(expectedUser);
    }

    @Test
    void shouldThrowIfUserForDeleteDoesNotExist() {
        when(mockUserRepo.findById(2))
                .thenReturn(Optional.empty());
        String expectedMessage = "Попытка удалить несуществующего пользователя";

        String actualMessage = assertThrows(
                EntityDoesNotExistException.class,
                () -> userService.delete(2)
        ).getMessage();

        verify(mockUserRepo, times(1))
                .findById(2);
        assertEquals(expectedMessage, actualMessage);
    }

}
