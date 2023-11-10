package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(int id);

    List<User> findAll();

    List<String> findAllEmails();

    void delete(int id);
}
