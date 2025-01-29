package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
public class MpaDbStorageTests {

    @Autowired
    private MpaDbStorage storage;


    @DisplayName("Получение возрастного рейтинга по id")
    @Test
    public void findById() {
        Optional<Mpa> actual = storage.findById(1);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @DisplayName("Получение всех возрастных рейтингов")
    @Test
    public void findAll() {
        Collection<Mpa> actual = storage.findAll();
        assertThat(actual)
                .isNotEmpty()
                .hasSize(5);

        assertThat(actual.stream().findFirst())
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G")
                );

    }
}
