package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FilmController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmTests {
    private Film film;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .id(1L)
                .name("John Wick")
                .description("The bad guy is taking revenge for the dog")
                .releaseDate(LocalDate.parse("2014-08-20"))
                .duration(88)
                .build();
    }

    @DisplayName("POST /films. Добавление корректного фильма")
    @Order(1)
    @Test
    void addFilm() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
    }

    @DisplayName("GET /users. Получение всех фильмов")
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
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(film)));
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
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()));
    }

    @DisplayName("POST /films. Продолжительность меньше 0")
    @Test
    void addFilmDurationIncorrect() throws Exception {
        film.setDuration(-1);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("PUT /films. Обновление фильма")
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
        film.setId(12L);

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()));
    }

    @DisplayName("PUT /films. Обновление фильма, параметры с null")
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
}
