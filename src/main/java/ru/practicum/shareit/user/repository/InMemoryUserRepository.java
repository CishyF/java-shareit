package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private int idCounter = 0;
    private final Map<Integer, User> userById = new HashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() != null && findById(user.getId()).isPresent()) {
            throw new EntityAlreadyExistsException("Пользователь с таким id уже существует");
        }
        final int generatedUserId = ++idCounter;
        user.setId(generatedUserId);
        userById.put(generatedUserId, user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(userById.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userById.values());
    }

    @Override
    public void delete(int id) {
        userById.remove(id);
    }
}
