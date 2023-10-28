package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(int id);

    List<User> findAll();

    void delete(int id);
}
