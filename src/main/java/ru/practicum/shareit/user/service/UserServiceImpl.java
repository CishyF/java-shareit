package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.util.UserMapper;
import ru.practicum.shareit.user.util.UserPatchUpdater;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserPatchUpdater userPatchUpdater;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.dtoToUser(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.userToDto(savedUser);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.usersToDtos(users);
    }

    @Override
    public UserDto findById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка получения несуществующего пользователя"));
        return userMapper.userToDto(user);
    }

    @Override
    public UserDto update(int id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Попытка обновить несуществующего пользователя"));
        String email = dto.getEmail();
        if (email != null && !user.getEmail().equals(email) && isEmailExists(email)) {
            throw new EntityAlreadyExistsException("Попытка присвоить пользователю уже использованную почту");
        }
        userPatchUpdater.updateUser(user, dto);
        return userMapper.userToDto(user);
    }

    @Override
    public void delete(int id) {
        userRepository.delete(id);
    }

    @Override
    public boolean isEmailExists(String email) {
        return findAll().stream()
                .map(UserDto::getEmail)
                .anyMatch(email::equals);
    }
}
