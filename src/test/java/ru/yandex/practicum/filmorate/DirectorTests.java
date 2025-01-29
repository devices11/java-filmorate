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
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.model.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
@WebMvcTest(DirectorController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DirectorTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("POST /directors - Добавление режиссера")
    @Test
    void addDirector() throws Exception {
        Director director = Director.builder().id(11).name("авпррар").build();

        mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
//                .andExpect(jsonPath("$.name").value(director.getName()));
    }

    @DisplayName("POST /directors. Добавление режиссера. name не заполнен")
    @Test
    void addDirectorBadRequest() throws Exception {
        mockMvc.perform(post("/directors").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Director.builder().name("").build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("PUT /directors - Обновление режиссера")
    @Test
    void updateDirector() throws Exception {
        Director director = Director.builder().id(2).name("Козлов").build();

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(director.getId()))
                .andExpect(jsonPath("$.name").value(director.getName()));
    }

    @DisplayName("PUT /directors - Обновление режиссера. id не существует")
    @Test
    void updateDirectorNotFound() throws Exception {
        Director director = Director.builder().id(211).name("Козлов").build();

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /directors/{id}. Получение режиссера по id")
    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/directors/1").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Лучший"));
    }

    @DisplayName("GET /directors/{id}. Получение режиссера, id не существует")
    @Test
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/directors/111").contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /directors. Получение всех режиссеров")
    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/directors").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Лучший"));
    }

    @DisplayName("DELETE /directors - Удаление режиссера")
    @Test
    void deleteDirector() throws Exception {
        mockMvc.perform(delete("/directors/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("DELETE /directors - Удаление режиссера. id не существует")
    @Test
    void deleteDirectorNotFound() throws Exception {
        mockMvc.perform(delete("/directors/1111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
