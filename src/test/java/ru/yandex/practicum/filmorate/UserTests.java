package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureDataJdbc
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
                .andDo(result -> {
                    User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                    user.setId(userDb.getId());
                })
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("GET /users. Получение всех пользователей")
    @Order(2)
    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/users").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").exists());
    }

    @DisplayName("POST /users. Пустое имя")
    @Order(3)
    @Test
    void addUserNameBlank() throws Exception {
        user.setName("");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(user.getLogin()));
    }

    @DisplayName("GET /users/{id}. Получение пользователя по id")
    @Order(4)
    @Test
    void getUsersById() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(result -> {
                    User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                    user.setId(userDb.getId());
                });

        mockMvc.perform(get("/users/" + user.getId()).contentType("application/json"))
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
        user.setId(1112L);

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("PUT /users. Обновление пользователя, параметры null")
    @Order(6)
    @Test
    void updateUserWithNull() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(result -> {
                    User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                    user.setId(userDb.getId());
                });

        User updateUser = user.toBuilder()
                .id(user.getId())
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
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/users/1/friends").contentType("application/json"))
                .andExpect(jsonPath("$.[0].id").value(2));
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
                .andExpect(jsonPath("$.[0].id").value(2));

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

    @DisplayName("DELETE /users/{id}. Удаление пользователя по id")
    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(result -> {
                    User userDb = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
                    user.setId(userDb.getId());
                })
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("DELETE /users/{id}. Удаление пользователя по id, id не найден")
    @Test
    void deleteUserIdNotFound() throws Exception {
        mockMvc.perform(delete("/users/1234").contentType(MediaType.APPLICATION_JSON))
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

    @DisplayName("GET /users/{id}/feed. Получение ленты по несуществующему id")
    @Test
    void getEventNotExistUserId() throws Exception {
        mockMvc.perform(get("/users/99999/feed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}