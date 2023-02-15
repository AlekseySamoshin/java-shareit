package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users;
    private long generatedId = 1;

    @Autowired
    public InMemoryUserStorage (HashMap<Long, User> users) {
        this.users = users;
    }

    @Override
    public User addUser(User user) {
        if (checkEmailIsAvailable(user.getId(), user.getEmail()) == false) {
            log.warn("Ошибка добавления пользователя. Email занят.");
            throw new ConflictException("Email " + user.getEmail() + " уже занят");
        }
        if(user.getId() == null) {
            user.setId(generateId());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь id=" + user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (checkEmailIsAvailable(user.getId(), user.getEmail()) == true) {
            users.put(user.getId(), user);
            return user;
        }
        log.warn("Ошибка обновления пользователя. Email занят.");
        throw new ConflictException("Email " + user.getEmail() + " уже занят");
    }

    @Override
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User deleteUserById(Long userId) {
        User user = users.get(userId);
        users.remove(userId);
        log.info("Пользователь id=" + userId + " удален");
        return user;
    }

    @Override
    public boolean checkEmailIsAvailable(Long userId, String checkedEmail) {
        for (User user : users.values()) {
            if (user.getEmail().equals(checkedEmail) && !(user.getId() == userId)) return false;
        }
        return true;
    }

    private long generateId() {
        return generatedId++;
    }
}
