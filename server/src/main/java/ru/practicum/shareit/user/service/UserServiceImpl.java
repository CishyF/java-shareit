package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.mapping.UserPatchUpdater;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserPatchUpdater userPatchUpdater;

    @Override
    public User create(UserDtoRequest userDto) {
        User user = userMapper.dtoRequestToUser(userDto);
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получения несуществующего пользователя"));
    }

    @Override
    public User update(int id, UserDtoRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка обновить несуществующего пользователя"));
        String email = dto.getEmail();
        String userEmail = user.getEmail();
        if (email != null && !userEmail.equals(email) && isEmailExists(email)) {
            throw new EntityAlreadyExistsException("Попытка присвоить пользователю уже использованную почту");
        }
        userPatchUpdater.updateUser(user, dto);
        return userRepository.save(user);
    }

    @Override
    public void delete(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка удалить несуществующего пользователя"));
        userRepository.delete(user);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.countByEmailEquals(email) != 0;
    }
}
