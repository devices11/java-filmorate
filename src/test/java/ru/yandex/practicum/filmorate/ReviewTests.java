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
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.model.Review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
@WebMvcTest(controllers = ReviewController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("POST /reviews - Создание отзыва")
    void createReview() throws Exception {
        Review review = Review.builder()
                .userId(1L)
                .filmId(203L)
                .content("Отличный фильм!")
                .isPositive(true)
                .build();

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Отличный фильм!"));
    }


    @Test
    @Order(2)
    @DisplayName("GET /reviews?filmId={filmId}&count={count} - Получение отзывов по фильму")
    void getReviewsByFilm() throws Exception {
        mockMvc.perform(get("/reviews")
                        .param("filmId", "203")
                        .param("count", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Отличный фильм!"));
    }

    @Test
    @Order(3)
    @DisplayName("PUT /reviews - Изменение отзыва")
    void updateReview() throws Exception {
        Review updatedReview = Review.builder()
                .reviewId(1L)
                .content("Фильм оказался унылым г...")
                .isPositive(false)
                .build();

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReview)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.content").value("Фильм оказался унылым г..."))
                .andExpect(jsonPath("$.isPositive").value(false));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /reviews/{id}/like/{userId} - Добавление лайка отзыву")
    void addReviewLike() throws Exception {
        mockMvc.perform(put("/reviews/1/like/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(1));
    }

    @Test
    @Order(5)
    @DisplayName("PUT /reviews/{id}/dislike/{userId} - Добавление дизлайка отзыву")
    void addReviewDislike() throws Exception {
        mockMvc.perform(put("/reviews/1/dislike/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(-1));
    }

    @Test
    @Order(6)
    @DisplayName("PUT /reviews/{id}/dislike/{userId} - Удаление дизлайка с отзыва")
    void removeReviewDislike() throws Exception {
        mockMvc.perform(delete("/reviews/1/dislike/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    @DisplayName("DELETE /reviews/{id}. Удаление отзыва")
    void removeReview() throws Exception {
        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET users/{id}/feed. Получение события удаления ревью")
    void getDeleteEventReview() throws Exception {
        mockMvc.perform(get("/users/1/feed").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[2].entityId").value(1))
                .andExpect(jsonPath("$[2].operation").value("REMOVE"));
    }

    @Test
    @DisplayName("GET users/{id}/feed. Получение события создания ревью")
    void getEventcreateReview() throws Exception {
        mockMvc.perform(get("/users/1/feed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[0].entityId").value(1))
                .andExpect(jsonPath("$[0].operation").value("ADD"));
    }

    @Test
    @DisplayName("GET users/{id}/feed. Получения события обновления ревью")
    void getEventRemoveReview() throws Exception {
        mockMvc.perform(get("/users/1/feed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[1].entityId").value(1))
                .andExpect(jsonPath("$[1].operation").value("UPDATE"));
    }
}