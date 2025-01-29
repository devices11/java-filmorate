package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final ValidationException e) {
        return new ErrorResponse(
                "Ошибка с входным параметром.", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(final NotFoundException e) {
        return new ErrorResponse(
                "Ошибка поиска объекта.", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateObject(final DataIntegrityViolationException e) {
        return new ErrorResponse(
                "Ошибка с входным параметром.", e.getMessage()
        );
    }
}
