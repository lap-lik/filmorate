package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {
    private static final String INVALIDED_DESCRIPTION =
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" +
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC" +
            "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" +
            "1";
    private Validator validator;
    private final Film film = Film.builder()
            .id(1)
            .name("nisi eiusmod")
            .description("adipisicing")
            .releaseDate(LocalDate.of(1967, 3, 25))
            .duration(100)
            .build();

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void validateFilmNameEmpty() {
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Название фильма не может быть пустым.", violations.iterator().next().getMessage());
    }

    @Test
    void validateCreateFilmDescriptionMaxSize() {
        film.setDescription(INVALIDED_DESCRIPTION);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания — 200 символов.", violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmReleaseDateInvalided() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Дата релиза фильма не должна быть раньше 28 декабря 1895 года.",
                violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmDurationInvalided() {
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительной.",
                violations.iterator().next().getMessage());
    }
}