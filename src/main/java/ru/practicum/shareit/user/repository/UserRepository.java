package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    void updateEmail(String email, String newEmail);

    Optional<User> findById(int id);

    List<User> findAll();

    List<String> findAllEmails();

    void delete(int id);
}
