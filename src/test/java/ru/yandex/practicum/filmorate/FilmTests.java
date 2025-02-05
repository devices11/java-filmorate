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
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
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
        Director director = Director.builder()
                .id(1)
                .name("Лучший")
                .build();
        film = Film.builder()
                .id(1L)
                .name("John Wick")
                .description("The bad guy is taking revenge for the dog")
                .releaseDate(LocalDate.parse("2014-08-20"))
                .duration(88)
                .mpa(mpa)
                .genres(List.of(genre))
                .directors(List.of(director))
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
        Director director = Director.builder()
                .id(1)
                .name("Лучший")
                .build();
        Film updateFilm = Film.builder()
                .name("TestName")
                .description("Test description")
                .releaseDate(LocalDate.parse("2011-08-20"))
                .duration(188)
                .mpa(mpa)
                .genres(List.of(genre))
                .directors(List.of(director))
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
                .andExpect(status().isOk());
    }

    @DisplayName("DELETE /films/{id}/like/{userId}. Удаление лайка к фильму по id")
    @Test
    void deleteLikeNoIdFilm() throws Exception {
        mockMvc.perform(delete("/films/11111/like/1").contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @DisplayName("DELETE /films/{id}. Удаление фильма по id")
    @Test
    void deleteFilm() throws Exception {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("PG")
                .build();
        Genre genre = Genre.builder()
                .id(1)
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
                        .content(objectMapper.writeValueAsString(deleteFilm)))
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

    @DisplayName("GET /films/popular. Получение популярных фильмов, параметры не указаны")
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

    @DisplayName("GET /films/popular. Получение популярных фильмов, count указан, жанр и год не указаны")
    @Order(11)
    @Test
    void findPopularFilms() throws Exception {
        mockMvc.perform(get("/films/popular?count=1").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @DisplayName("GET /films/popular. Получение популярных фильмов, не найдено фильмов по параметрам")
    @Test
    @Order(12)
    void findPopularFilmsNoResults() throws Exception {
        mockMvc.perform(get("/films/popular?count=10&year=1900").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace("[]"));
    }

    @DisplayName("GET /films/popular. Получение популярных фильмов с фильтром по жанру и году")
    @Test
    @Order(13)
    void findPopularFilmsByGenreAndYear() throws Exception {
        for (int i = 0; i < 12; i++) {
            mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(film)));
        }
        for (int i = 0; i < 12; i++) {
            String path = "/films/" + i + "/like/1";
            mockMvc.perform(put(path).contentType("application/json"));
        }

        mockMvc.perform(get("/films/popular?count=10&genreId=1&year=2014").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @DisplayName("GET /films/director/{directorId}. " +
            "Получение списка фильмов режиссера отсортированных по количеству году выпуска")
    @Test
    void findByDirectorIdSortReleaseDate() throws Exception {
        mockMvc.perform(get("/films/director/4").queryParam("sortBy", "year")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200))
                .andExpect(jsonPath("$[1].id").value(201))
                .andExpect(jsonPath("$[2].id").value(202));
    }

    @DisplayName("GET /films/director/{directorId}. " +
            "Получение списка фильмов режиссера отсортированных по количеству лайков")
    @Test
    void findByDirectorIdSorLikes() throws Exception {
        mockMvc.perform(get("/films/director/4").queryParam("sortBy", "likes")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(202))
                .andExpect(jsonPath("$[1].id").value(201))
                .andExpect(jsonPath("$[2].id").value(200));
    }

    @DisplayName("GET /films/director/{directorId}. " +
            "Получение списка фильмов режиссера отсортированных по дефолту")
    @Test
    void findByDirectorIdDefaultSort() throws Exception {
        mockMvc.perform(get("/films/director/4")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200))
                .andExpect(jsonPath("$[1].id").value(201))
                .andExpect(jsonPath("$[2].id").value(202));
    }

    @DisplayName("GET /films/director/{directorId}. " +
            "Получение списка фильмов режиссера отсортированных по количеству лайков")
    @Test
    void findByDirectorIdNotFound() throws Exception {
        mockMvc.perform(get("/films/director/11111").queryParam("sortBy", "year")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /films/common. Получение общих фильмов, userId и friendId указаны")
    @Test
    void findCommonFilms() throws Exception {
        mockMvc.perform(get("/films/common?userId=2&friendId=3").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @DisplayName("GET /films/common. Получение общих фильмов, params не указаны")
    @Test
    public void findCommonFilmsNoParams() throws Exception {
        mockMvc.perform(get("/films/common").contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /films/common. Получение общих фильмов, пользователей не существует")
    @Test
    public void findCommonFilmsNoUserId() throws Exception {
        mockMvc.perform(get("/films/common?userId=99999&friendId=666666").contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /films/search. Поиск фильмов по названию")
    @Test
    public void searchFilmsByTitle() throws Exception {
        film.setName("Самый худший фильм");
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        mockMvc.perform(get("/films/search?query=дший&by=title").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Самый худший фильм"));
    }

    @DisplayName("GET /films/search. Поиск фильмов без режисера, по названию и имени режисера")
    @Test
    public void searchFilmsByTitleAndDirectorNotFoundDirector() throws Exception {
        film.setName("New film without director");
        film.setDirectors(List.of());
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        mockMvc.perform(get("/films/search?query=ilm without dir&by=title,director").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("New film without director"));
    }

    @DisplayName("GET /films/search. Поиск фильмов по имени режисера")
    @Test
    public void searchFilmsByDirectors() throws Exception {
        mockMvc.perform(get("/films/search?query=Пупкин&by=director").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Доспехи бога 3"))
                .andExpect(jsonPath("$[1].name").value("Доспехи бога 2"))
                .andExpect(jsonPath("$[2].name").value("Доспехи бога 1"));
    }

    @DisplayName("GET /films/search. Поиск фильмов по названию и имени режисера")
    @Test
    public void searchFilmsByTitleAndDirector() throws Exception {
        film.setName("Самый лучший фильм от Петрова");
        Director director = Director.builder()
                .id(2)
                .build();
        film.setDirectors(List.of(director));
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        mockMvc.perform(get("/films/search?query=Петр&by=title,director").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Самый лучший фильм от Петрова"));
    }

    @DisplayName("GET /films/search. Поиск фильмов по имени режисера, отправлен пустой запрос")
    @Test
    public void searchFilmsNotQuery() throws Exception {
        mockMvc.perform(get("/films/search?query= &by=director").contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /films/search. Поиск фильмов по имени режисера, отправлен не правильный параметр")
    @Test
    public void searchFilmsNotValidParams() throws Exception {
        mockMvc.perform(get("/films/search?query=кин&by=директор").contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /users/{id}/recommendations. Проверка рекомендаций для пользователя с лайками")
    @Test
    public void getRecommendationsForUserWithLikes() throws Exception {
        mockMvc.perform(get("/users/1/recommendations").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((1)))
                .andExpect(jsonPath("$[0].id").value(201));
    }

    @DisplayName("GET /users/{id}/recommendations. Пользователь без лайков должен получить пустой список")
    @Test
    public void getRecommendationsForUserWithoutLikes() throws Exception {
        mockMvc.perform(get("/users/2/recommendations").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @DisplayName("GET /users/{id}/recommendations. Запрос рекомендаций для несуществующего пользователя")
    @Test
    public void getRecommendationsForNonExistentUser() throws Exception {
        mockMvc.perform(get("/users/999/recommendations").contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /users/{id}/recommendations. Пользователю которому нечего рекомендовать")
    @Test
    public void getRecommendationsForUserWithNoSimilarUsers() throws Exception {
        mockMvc.perform(get("/users/3/recommendations").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @DisplayName("GET /users/{id}/feed.  вызов события с неправильным id")
    @Test
    void wrongUserIdEvent() throws Exception {
        mockMvc.perform(get("/users/9999999/feed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}
