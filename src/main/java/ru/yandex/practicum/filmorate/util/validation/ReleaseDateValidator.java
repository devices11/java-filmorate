package ru.yandex.practicum.filmorate.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.util.validation.annotation.ReleaseDate;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private static final LocalDate CINEMA_BIRTH_DAY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return !value.isBefore(CINEMA_BIRTH_DAY);
        }
        return true;
    }
}
