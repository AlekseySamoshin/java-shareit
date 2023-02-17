package ru.practicum.shareit.user.storage;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(Long userId);

    User deleteUserById(Long userId);

    boolean checkEmailIsAvailable(Long userId, String checkedEmail);
}
