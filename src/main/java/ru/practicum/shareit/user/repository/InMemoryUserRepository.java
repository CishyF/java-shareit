package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.user.entity.User;

import java.util.*;

@Deprecated
public class InMemoryUserRepository {

    private int idCounter = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    public User save(User user) {
        if (user.getId() != null && findById(user.getId()).isPresent()) {
            throw new EntityAlreadyExistsException("Пользователь с таким id уже существует");
        }
        final int generatedUserId = ++idCounter;
        user.setId(generatedUserId);
        users.put(generatedUserId, user);
        emails.add(user.getEmail());
        return user;
    }

    public void updateEmail(String email, String newEmail) {
        if (emails.contains(email)) {
            emails.remove(email);
            emails.add(newEmail);
        }
    }

    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<String> findAllEmails() {
        return new ArrayList<>(emails);
    }

    public void delete(int id) {
        findById(id).ifPresent(user -> emails.remove(user.getEmail()));
        users.remove(id);
    }
}
