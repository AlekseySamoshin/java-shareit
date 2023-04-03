package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserDtoMapper userDtoMapper;

    @InjectMocks
    UserService userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user2 = new User();
        userDto1 = new UserDto();
        userDto2 = new UserDto();
        user1.setId(1L);
        user1.setName("userName1");
        user1.setEmail("user1@email.com");
        user2.setId(2L);
        user2.setName("userName2");
        user2.setEmail("user1@email.com");
        userDto1.setId(1L);
        userDto1.setName("userName1");
        userDto1.setEmail("user1@email.com");
        userDto2.setId(2L);
        userDto2.setName("");
        userDto2.setEmail("email.com");
    }

    @Test
    void getUsers() {
        List<User> users = List.of(user1);
        when(userRepository.findAll()).thenReturn(users);
        when(userDtoMapper.mapUser(any())).thenReturn(userDto1);
        List<UserDto> testList = userService.getUsers();
        assertEquals(testList.get(0).getId(), users.get(0).getId());
    }


    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userDtoMapper.mapUser(any())).thenReturn(userDto1);

        UserDto testUser = userService.getUserById(1L);
        assertEquals(testUser.getId(), user1.getId());
        assertEquals(testUser.getName(), user1.getName());
        assertEquals(testUser.getEmail(), user1.getEmail());
    }

    @Test
    void addUser() {
        when(userRepository.save(any())).thenReturn(user1);
        when(userDtoMapper.mapUser(any())).thenReturn(userDto1);

        UserDto testUser = userService.addUser(userDto1);
        assertEquals(testUser.getId(), userDto1.getId());
        assertEquals(testUser.getName(), user1.getName());
        assertEquals(testUser.getEmail(), user1.getEmail());

        Exception exception = assertThrows(WrongDataException.class,
                () -> userService.addUser(userDto2));
        assertEquals("Не указан email! Не указан логин! ", exception.getMessage());
    }

    @Test
    void addUserWrongData() {
        userDto1.setName("");
        when(userRepository.save(any())).thenReturn(user1);
        when(userDtoMapper.mapUser(any())).thenReturn(userDto1);

        assertThrows(WrongDataException.class, () -> userService.addUser(userDto1));
    }

    @Test
    void updateUser() {
        when(userRepository.save(any())).thenReturn(user1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userDtoMapper.mapUser(any())).thenReturn(userDto1);

        UserDto testUser = userService.updateUser(1L, userDto1);
        assertEquals(testUser.getId(), userDto1.getId());
        assertEquals(testUser.getName(), user1.getName());
        assertEquals(testUser.getEmail(), user1.getEmail());
    }
}