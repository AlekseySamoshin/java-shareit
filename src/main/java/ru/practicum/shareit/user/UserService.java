package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(userDtoMapper::mapUser)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        User userOptional = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден")
        );
        User user = userOptional;
        return userDtoMapper.mapUser(user);
    }

    public UserDto addUser(UserDto userDto) {
        User user = userDtoMapper.mapDto(userDto);
        validateUserDto(userDto);
        user = userRepository.save(user);
        return userDtoMapper.mapUser(user);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        User user = userOptional.get();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        user = userRepository.save(user);
        return userDtoMapper.mapUser(user);
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
        Optional<User> user = userRepository.findById(userId);
        userRepository.deleteById(userId);
        return userDtoMapper.mapUser(user.get());
    }
}
