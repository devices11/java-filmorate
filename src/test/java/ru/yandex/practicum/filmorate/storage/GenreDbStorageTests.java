package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureDataJdbc
public class GenreDbStorageTests {

    @Autowired
    private GenreDbStorage genreDbStorage;

    @DisplayName("Получение жанра по id")
    @Test
    public void findById() {
        Optional<Genre> actual = genreDbStorage.findById(1);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @DisplayName("Получение всех жанров")
    @Test
    public void findAll() {
        Collection<Genre> actual = genreDbStorage.findAll();
        assertThat(actual)
                .isNotEmpty()
                .hasSize(6);

        assertThat(actual.stream().findFirst())
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
                );

    }
}
