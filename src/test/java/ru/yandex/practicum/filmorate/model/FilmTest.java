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
    private Film film;
    private static Validator validator;
    private final static Integer ID = 1;
    private final static String NAME = "nisi eiusmod";
    private final static String DESCRIPTION = "adipisicing";
    private final static LocalDate RELEASE_DATA = LocalDate.of(1967, 3, 25);
    private final static Integer DURATION = 100;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void validateFilmNameEmpty() {
        film = Film.builder()
                .id(ID)
                .name("")
                .description(DESCRIPTION)
                .releaseDate(RELEASE_DATA)
                .duration(DURATION)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Название фильма не может быть пустым.", violations.iterator().next().getMessage());
    }

    @Test
    void validateCreateFilmDescriptionMaxSize() {
        film = Film.builder()
                .id(ID)
                .name(NAME)
                .description("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" +
                        "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC" +
                        "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" +
                        "1")
                .releaseDate(RELEASE_DATA)
                .duration(DURATION)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания — 200 символов.", violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmReleaseDateInvalided() {
        film = Film.builder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(DURATION)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Дата релиза фильма не должна быть раньше 28 декабря 1895 года.",
                violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmDurationInvalided() {
        film = Film.builder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .releaseDate(RELEASE_DATA)
                .duration(-1)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительной.",
                violations.iterator().next().getMessage());
    }
}