package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"ru.yandex.practicum.filmorate"})
@WebMvcTest(controllers = FilmController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmTests {
    private Film film;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        film = Film.builder()
                .id(1L)
                .name("John Wick")
                .description("The bad guy is taking revenge for the dog")
                .releaseDate(LocalDate.parse("2014-08-20"))
                .duration(88)
                .build();
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .build();
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
    }

    @DisplayName("POST /films. Добавление корректного фильма")
    @Order(1)
    @Test
    void addFilm() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("GET /films. Получение всех фильмов")
    @Order(2)
    @Test
    void getAllFilms() throws Exception {
        List<String> resp = new ArrayList<>();
        resp.add(objectMapper.writeValueAsString(film));

        mockMvc.perform(get("/films").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(resp.toString()));
    }

    @DisplayName("POST /films. Параметры с null")
    @Order(3)
    @Test
    void addFilmWithNull() throws Exception {
        film.setId(2L);
        film.setDescription(null);
        film.setReleaseDate(null);
        film.setDuration(null);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("GET /films/{id}. Получение фильма по id")
    @Order(4)
    @Test
    void getFilmById() throws Exception {
        mockMvc.perform(get("/films/1").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("GET /films/{id}. Получение фильма по id, фильм не найден")
    @Test
    void getFilmByIdFilmNotFound() throws Exception {
        mockMvc.perform(get("/films/1111111").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("POST /films. Имя пустое")
    @Test
    void addFilmNameBlank() throws Exception {
        film.setName("");

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /films. Имя null")
    @Test
    void addFilmNameNull() throws Exception {
        film.setName(null);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /films. Описание больше 200 символов")
    @Test
    void addFilmDescriptionMore200Char() throws Exception {
        film.setDescription("a".repeat(201));

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /films. Дата релиза меньше 28.12.1895")
    @Test
    void addFilmReleaseDateIncorrect() throws Exception {
        film.setReleaseDate(LocalDate.parse("1895-12-27"));

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /films. Продолжительность меньше 0")
    @Test
    void addFilmDurationIncorrect() throws Exception {
        film.setDuration(-1);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("PUT /films. Обновление фильма, параметры с null")
    @Order(5)
    @Test
    void updateFilmWithNull() throws Exception {
        Film updateFilm = film.toBuilder()
                .id(1L)
                .name(null)
                .description(null)
                .releaseDate(null)
                .duration(null)
                .build();

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilm)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("PUT /films. Обновление фильма")
    @Order(6)
    @Test
    void updateFilm() throws Exception {
        film.setId(1L);
        film.setName("John Wick2");
        film.setDescription("The bad guy is taking revenge for the dog2");
        film.setReleaseDate(LocalDate.parse("2014-08-21"));
        film.setDuration(8);

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("PUT /films. Обновление фильма, id не существует")
    @Test
    void updateFilmNoId() throws Exception {
        film.setId(112L);

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }


    @DisplayName("PUT /films/{id}/like/{userId}. Добавление лайка к фильму по id")
    @Order(7)
    @Test
    void setLike() throws Exception {
        mockMvc.perform(put("/films/1/like/1").contentType("application/json"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/films/1").contentType("application/json"))
                .andExpect(jsonPath("$.likes").value(1));
    }

    @DisplayName("PUT /films/{id}/like/{userId}. Добавление лайка к фильму, id не существует")
    @Test
    void setLikeNoIdFilm() throws Exception {
        mockMvc.perform(put("/films/11111/like/1").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("PUT /films/{id}/like/{userId}. Добавление лайка к фильму, id пользователя не существует")
    @Test
    void setLikeNoIdUser() throws Exception {
        mockMvc.perform(put("/films/1/like/111111").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("DELETE /films/{id}/like/{userId}. Удаление лайка к фильму по id")
    @Order(8)
    @Test
    void deleteLike() throws Exception {
        mockMvc.perform(delete("/films/1/like/1").contentType("application/json"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/films/1").contentType("application/json"))
                .andExpect(jsonPath("$.likes").isEmpty())
                .andReturn();
    }

    @DisplayName("DELETE /films/{id}/like/{userId}. Удаление лайка к фильму по id")
    @Test
    void deleteLikeNoIdFilm() throws Exception {
        mockMvc.perform(delete("/films/11111/like/1").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("DELETE /films/{id}/like/{userId}. Удаление лайка к фильму, id пользователя не существует")
    @Test
    void deleteLikeNoIdUser() throws Exception {
        mockMvc.perform(delete("/films/1/like/111111").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("GET /films/popular. Получение популярных фильмов, список пустой")
    @Order(9)
    @Test
    void findPopularFilmsNoPopular() throws Exception {
        mockMvc.perform(get("/films/popular").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace("[]"));
    }

    @DisplayName("GET /films/popular. Получение популярных фильмов, count не указан")
    @Order(10)
    @Test
    void findPopularFilmsNoCount() throws Exception {
        for (int i = 0; i < 12; i++) {
            mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(film)));
        }
        for (int i = 0; i < 12; i++) {
            String path = "/films/" + i + "/like/1";
            mockMvc.perform(put(path).contentType("application/json"));
        }

        mockMvc.perform(get("/films/popular").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10));
    }

    @DisplayName("GET /films/popular. Получение популярных фильмов, count указан")
    @Order(11)
    @Test
    void findPopularFilms() throws Exception {
        mockMvc.perform(get("/films/popular?count=5").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5));
    }

}
