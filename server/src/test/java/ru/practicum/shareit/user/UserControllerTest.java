package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private UserDto user1Dto;
    private UserDto user2Dto;

    @BeforeEach
    void setUp() {
        user1Dto = new UserDto();
        user2Dto = new UserDto();
        user1Dto.setId(1L);
        user1Dto.setName("user1");
        user1Dto.setEmail("test1@email.ru");
        user2Dto.setId(2L);
        user2Dto.setName("user2");
        user2Dto.setEmail("test2@email.ru");
    }

    @Test
    void addUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(user1Dto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1Dto.getName())))
                .andExpect(jsonPath("$.email", is(user1Dto.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        Long userId = 1L;
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(user1Dto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(user1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1Dto.getName())))
                .andExpect(jsonPath("$.email", is(user1Dto.getEmail())));
    }

    @Test
    void getUsers() throws Exception {
        when(userService.getUsers())
                .thenReturn(List.of(user1Dto, user2Dto));

        mockMvc.perform(get("/users")
                        .content(mapper.writeValueAsString(user1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].id", is(user2Dto.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(user2Dto.getName())))
                .andExpect(jsonPath("$[1].email", is(user2Dto.getEmail())));
    }

    @Test
    void getUserById() throws Exception {
        Long userId = 1L;
        when(userService.getUserById(anyLong()))
                .thenReturn(user1Dto);

        mockMvc.perform(get("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(user1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1Dto.getName())))
                .andExpect(jsonPath("$.email", is(user1Dto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        Long userId = 1L;
        when(userService.deleteUserById(anyLong()))
                .thenReturn(user1Dto);

        mockMvc.perform(delete("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(user1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1Dto.getName())))
                .andExpect(jsonPath("$.email", is(user1Dto.getEmail())));
    }
}
