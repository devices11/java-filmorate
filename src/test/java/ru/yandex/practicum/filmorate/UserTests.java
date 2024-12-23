package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.yandex.practicum.filmorate"})
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
                .andExpect(status().isCreated())
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
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(newUser)));
    }

    @DisplayName("GET /users/{id}. Получение пользователя по id")
    @Order(4)
    @Test
    void getUsersById() throws Exception {
        mockMvc.perform(get("/users/1").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("GET /users/{id}. Получение пользователя по id, пользователь не найден")
    @Test
    void getUsersByIdUserNotFound() throws Exception {
        mockMvc.perform(get("/users/1111111").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
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
    @Order(5)
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
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("PUT /users. Обновление пользователя, параметры null")
    @Order(6)
    @Test
    void updateUserWithNull() throws Exception {
        User updateUser = user.toBuilder()
                .id(1L)
                .email(null)
                .login(null)
                .name(null)
                .birthday(null)
                .build();
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("PUT /users/{id}/friends/{friendId}. Добавление в друзья")
    @Order(7)
    @Test
    void addFriend() throws Exception {
        mockMvc.perform(put("/users/1/friends/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friends").value(2));
        mockMvc.perform(get("/users/2").contentType("application/json"))
                .andExpect(jsonPath("$.friends").value(1));
    }

    @DisplayName("PUT /users/{id}/friends/{friendId}. Добавление в друзья, id не существует")
    @Test
    void addFriendIdNotFound() throws Exception {
        mockMvc.perform(put("/users/111111/friends/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("PUT /users/{id}/friends/{friendId}. Добавление в друзья, friendId не существует")
    @Test
    void addFriendFriendIdNotFound() throws Exception {
        mockMvc.perform(put("/users/1/friends/22222").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("GET /users/{id}/friends. Получение списка друзей")
    @Order(8)
    @Test
    void getFriends() throws Exception {
        mockMvc.perform(get("/users/1/friends").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].friends").value(1));

    }

    @DisplayName("GET /users/{id}/friends. Получение списка друзей, id не найден")
    @Test
    void getFriendsIdNotFound() throws Exception {
        mockMvc.perform(get("/users/11111/friends").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("DELETE /users/{id}/friends/{friendId}. Удаление из друзей")
    @Order(9)
    @Test
    void deleteFriend() throws Exception {
        mockMvc.perform(delete("/users/1/friends/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/users/1/friends").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/users/2/friends").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

    }

    @DisplayName("DELETE /users/{id}/friends/{friendId}. Удаление из друзей")
    @Test
    void deleteFriendIdNotFound() throws Exception {
        mockMvc.perform(delete("/users/11111/friends/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("DELETE /users/{id}/friends/{friendId}. Удаление из друзей")
    @Test
    void deleteFriendFriendIdNotFound() throws Exception {
        mockMvc.perform(delete("/users/1/friends/21111").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("GET /users/{id}/friends/common/{otherId}. Получение общего списка друзей")
    @Order(10)
    @Test
    void commonFriends() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(put("/users/1/friends/3").contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(put("/users/2/friends/3").contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/users/1/friends/common/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(3));
    }

    @DisplayName("GET /users/{id}/friends/common/{otherId}. Получение общего списка друзей, id не найден")
    @Order(10)
    @Test
    void commonFriendsIdNotFound() throws Exception {
        mockMvc.perform(get("/users/11111/friends/common/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("GET /users/{id}/friends/common/{otherId}. Получение общего списка друзей, otherId не найден")
    @Order(10)
    @Test
    void commonFriendsOtherIdNotFound() throws Exception {
        mockMvc.perform(get("/users/1/friends/common/21111").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

}