package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {
    private User user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .login("testLogin")
                .name("Kenny")
                .birthday(LocalDate.parse("1946-08-20"))
                .build();
    }

    @DisplayName("POST /users. Добавление корректного пользователя")
    @Order(1)
    @Test
    void addUser() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("GET /users. Получение всех пользователей")
    @Order(2)
    @Test
    void getAllUsers() throws Exception {
        List<String> resp = new ArrayList<>();
        resp.add(objectMapper.writeValueAsString(user));

        mockMvc.perform(get("/users").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(resp.toString()));
    }

    @DisplayName("POST /users. Пустое имя")
    @Order(3)
    @Test
    void addUserNameBlank() throws Exception {
        user.setName("");
        User newUser = user.toBuilder()
                .id(2L)
                .name(user.getLogin())
                .build();

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(newUser)));
    }

    @DisplayName("POST /users. Email некорректный")
    @Test
    void addUserBadEmail() throws Exception {
        user.setEmail("test.com");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /users. Email = null")
    @Test
    void addUserEmailNull() throws Exception {
        user.setEmail(null);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /users. Email пустой")
    @Test
    void addUserEmailBlank() throws Exception {
        user.setEmail("");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /users. Логин некорректный")
    @Test
    void addUserBadLogin() throws Exception {
        user.setLogin("dd dd");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /users. birthday в будущем")
    @Test
    void addUserBadBirthday() throws Exception {
        user.setBirthday(LocalDate.parse("2946-08-20"));

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("PUT /users. Обновление пользователя")
    @Test
    void updateUser() throws Exception {
        user.setId(2L);
        user.setEmail("test2@test.com");
        user.setLogin("testLogin2");
        user.setName("Kenny2");
        user.setBirthday(LocalDate.parse("1946-08-22"));
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("PUT /users. Обновление пользователя, id не существует")
    @Test
    void updateUserNoId() throws Exception {
        user.setId(12L);

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()));
    }

}


