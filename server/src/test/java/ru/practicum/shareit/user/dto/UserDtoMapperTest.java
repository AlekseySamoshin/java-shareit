package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class UserDtoMapperTest {

    @Mock
    User user;

    @Mock
    UserDto userDto;

    @Mock
    UserDtoMapper userDtoMapper;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@email.ru");
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user dto");
        userDto.setEmail("dto@email.ru");
        userDtoMapper = new UserDtoMapper();
    }

    @Test
    void mapUser() {
        UserDto newDto = userDtoMapper.mapUser(user);
        assertEquals(user.getId(), newDto.getId());
        assertEquals(user.getName(), newDto.getName());
        assertEquals(user.getEmail(), newDto.getEmail());
    }

    @Test
    void mapDto() {
        User newUser = userDtoMapper.mapDto(userDto);
        assertEquals(userDto.getName(), newUser.getName());
        assertEquals(userDto.getEmail(), newUser.getEmail());
    }
}