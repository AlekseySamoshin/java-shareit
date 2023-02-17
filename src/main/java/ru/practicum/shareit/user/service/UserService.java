package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserService(@Qualifier("InMemoryUserStorage") UserStorage userStorage, UserDtoMapper userDtoMapper) {
        this.userStorage = userStorage;
        this.userDtoMapper = userDtoMapper;
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(userDtoMapper::mapUser)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        return userDtoMapper.mapUser(userStorage.getUserById(userId));
    }

    public UserDto addUser(UserDto userDto) {
        User user = userDtoMapper.mapDto(userDto);
        return userDtoMapper.mapUser(userStorage.addUser(user));
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userStorage.getUserById(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userStorage.checkEmailIsAvailable(userId, userDto.getEmail()) == true) {
                user.setEmail(userDto.getEmail());
            } else {
                String message = "Email " + userDto.getEmail() + " уже занят";
                log.error(message);
                throw new ConflictException(message);
            }
        }
        return userDtoMapper.mapUser(userStorage.updateUser(user));
    }

    public void validateUserDto(UserDto userDto) throws WrongDataException {
        StringBuilder message = new StringBuilder();
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            message.append("Не указан email! ");
        }
        if (userDto.getName().isBlank()) {
            message.append("Не указан логин! ");
        }
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации пользователя: " + message.toString());
            throw new WrongDataException(message.toString());
        }
    }

    public UserDto deleteUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        userStorage.deleteUserById(userId);
        return userDtoMapper.mapUser(user);
    }
}
