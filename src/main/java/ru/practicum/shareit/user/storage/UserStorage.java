package ru.practicum.shareit.user.storage;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public interface UserStorage {
    public User addUser(User user);
    User updateUser(User user);
    public List<User> getUsers();
    public User getUserById(Long userId);

    public User deleteUserById(Long userId);

    boolean checkEmailIsAvailable(Long userId, String checkedEmail);
}
