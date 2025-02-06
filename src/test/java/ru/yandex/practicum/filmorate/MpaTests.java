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
import ru.yandex.practicum.filmorate.controller.MpaController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
@WebMvcTest(controllers = MpaController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MpaTests {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("GET /mpa/{id}. Получение возрастного рейтинга по id")
    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/mpa/1").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("G"));
    }

    @DisplayName("GET /mpa/{id}. Получение возрастного рейтинга, id не существует")
    @Test
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/mpa/111").contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /mpa. Получение всех возрастных рейтингов")
    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/mpa").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("G"));
    }
}