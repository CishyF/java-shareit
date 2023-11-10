package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private int idCounter = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
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

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<String> findAllEmails() {
        return new ArrayList<>(emails);
    }

    @Override
    public void delete(int id) {
        findById(id).ifPresent(value -> emails.remove(value.getEmail()));
        users.remove(id);
    }
}
