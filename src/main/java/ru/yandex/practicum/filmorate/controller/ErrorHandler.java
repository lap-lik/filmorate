package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidException;
import ru.yandex.practicum.filmorate.response.ErrorResponse;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(assignableTypes = {FilmController.class, UserController.class})
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse badRequestException(final BadRequestException exception) {

        log.warn("Exception: {}, Bad request: \n- {}", exception.getClass().getName(), exception.getMessage());

        return ErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidException.class, ConstraintViolationException.class})
    public ErrorResponse validateErrorException(final RuntimeException exception) {

        log.warn("Exception: {}, Validation error(s): \n{}", exception.getClass().getName(),
                Arrays.stream(exception.getMessage().split("&"))
                        .map(message -> "- " + message.trim())
                        .collect(Collectors.joining("\n")));

        return ErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse notFoundException(final NotFoundException exception) {

        log.warn("Exception: {}, Error(s): \n{}", exception.getClass().getName(),
                Arrays.stream(exception.getMessage().split("&"))
                        .map(message -> "- " + message.trim())
                        .collect(Collectors.joining("\n")));

        return ErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse throwableException(final Exception exception) {

        log.error("Exception: {}", exception.toString());
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }
}
