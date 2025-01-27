package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
@WebMvcTest(controllers = FilmController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmTests {
    private Film film;
    private User user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        film = Film.builder()
                .id(1L)
                .name("John Wick")
                .description("The bad guy is taking revenge for the dog")
                .releaseDate(LocalDate.parse("2014-08-20"))
                .duration(88)
                .mpa(mpa)
                .genres(List.of(genre))
                .build();
        user = User.builder()
                .login("test")
                .email("test@test.com")
                .build();

    }

    @DisplayName("POST /films. Добавление корректного фильма")
    @Order(1)
    @Test
    void addFilm() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(result -> {
                    Film filmDb = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
                    film.setId(filmDb.getId());
                })
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("GET /films. Получение всех фильмов")
    @Order(2)
    @Test
    void getAllFilms() throws Exception {
        mockMvc.perform(get("/films").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").exists());
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
                .andDo(result -> {
                    Film filmDb = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
                    film.setId(filmDb.getId());
                })
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("GET /films/{id}. Получение фильма по id")
    @Order(4)
    @Test
    void getFilmById() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(result -> {
                    Film filmDb = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
                    film.setId(filmDb.getId());
                });

        mockMvc.perform(get("/films/" + film.getId()).contentType("application/json"))
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
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(result -> {
                    Film filmDb = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
                    film.setId(filmDb.getId());
                });

        Film updateFilm = film.toBuilder()
                .id(film.getId())
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

        Mpa mpa = Mpa.builder()
                .id(2)
                .name("PG")
                .build();
        Genre genre = Genre.builder()
                .id(2)
                .name("Драма")
                .build();
        Film updateFilm = Film.builder()
                .name("TestName")
                .description("Test description")
                .releaseDate(LocalDate.parse("2011-08-20"))
                .duration(188)
                .mpa(mpa)
                .genres(List.of(genre))
                .build();
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(result -> {
                    Film filmDb = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
                    updateFilm.setId(filmDb.getId());
                });

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilm)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(updateFilm)));
    }

    @DisplayName("PUT /films. Обновление фильма, id не существует")
    @Test
    void updateFilmNoId() throws Exception {
        film.setId(11112L);

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(result -> {
                    Film filmDb = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
                    film.setId(filmDb.getId());
                })
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }


    @DisplayName("PUT /films/{id}/like/{userId}. Добавление лайка к фильму по id")
    @Order(7)
    @Test
    void setLike() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        mockMvc.perform(put("/films/1/like/1").contentType("application/json"))
                .andExpect(status().isNoContent());
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
    @Test
    void deleteLike() throws Exception {
        mockMvc.perform(delete("/films/1/like/1").contentType("application/json"))
                .andExpect(status().isNoContent());
    }

    @DisplayName("DELETE /films/{id}/like/{userId}. Удаление лайка к фильму по id")
    @Test
    void deleteLikeNoIdFilm() throws Exception {
        mockMvc.perform(delete("/films/11111/like/1").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("DELETE /films/{id}. Удаление фильма по id")
    @Order(7)
    @Test
    void deleteFilm() throws Exception {
        Mpa mpa = Mpa.builder()
                .id(3)
                .name("PG")
                .build();
        Genre genre = Genre.builder()
                .id(3)
                .name("Драма")
                .build();
        Film deleteFilm = Film.builder()
                .name("TestName")
                .description("Test description")
                .releaseDate(LocalDate.parse("2011-08-20"))
                .duration(188)
                .mpa(mpa)
                .genres(List.of(genre))
                .build();
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(result -> {
                    Film filmDb = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);
                    deleteFilm.setId(filmDb.getId());
                });

        mockMvc.perform(delete("/films/" + deleteFilm.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("DELETE /films/{id}. Удаление фильма по id, фильм не найден")
    @Test
    void deleteFilmIdNotFound() throws Exception {
        mockMvc.perform(delete("/films/222").contentType(MediaType.APPLICATION_JSON))
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
    @Test
    void findPopularFilmsNoPopular() throws Exception {
        mockMvc.perform(get("/films/popular").queryParam("count", "0").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace("[]"));
    }

    @DisplayName("GET /films/popular. Получение популярных фильмов, count не указан")
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
                .andExpect(jsonPath("$.length()").value(10));
    }

    @DisplayName("GET /films/popular. Получение популярных фильмов, count указан")
    @Order(11)
    @Test
    void findPopularFilms() throws Exception {
        mockMvc.perform(get("/films/popular?count=1").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

}
