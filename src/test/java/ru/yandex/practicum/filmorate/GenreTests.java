package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.controller.GenreController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
@WebMvcTest(controllers = GenreController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenreTests {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("GET /genres/{id}. Получение жанра по id")
    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/genres/1").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Комедия"));
    }

    @DisplayName("GET /genres/{id}. Получение жанра, id не существует")
    @Test
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/genres/111").contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /genres. Получение всех жанров")
    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/genres").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Комедия"));
    }
}
