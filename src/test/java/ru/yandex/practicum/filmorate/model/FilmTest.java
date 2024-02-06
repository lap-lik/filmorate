package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {
    public static final String INVALIDED_DESCRIPTION = "A".repeat(201);
    private Validator validator;
    private final FilmDTO filmDTO = FilmDTO.builder()
            .name("Star Wars. Episode I: The Phantom Menace")
            .description("Genre: Action, Adventure, Fantasy, Live Action, Science Fiction")
            .releaseDate(LocalDate.of(1999, 5, 19))
            .mpa(Mpa.builder().id(1L).build())
            .duration(136)
            .build();

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    @DisplayName("A test to check an empty film name.")
    void validateFilmNameEmpty() {

        filmDTO.setName("");

        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(filmDTO);
        String textFromValidation = getViolations(violations);

        assertEquals(1, violations.size());
        assertEquals(textFromValidation, "The name of the film must not be empty.");
    }


    @Test
    @DisplayName("A test to check the description of more than 200 characters.")
    void validateCreateFilmDescriptionMaxSize() {

        filmDTO.setDescription(INVALIDED_DESCRIPTION);

        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(filmDTO);
        String textFromValidation = getViolations(violations);

        assertEquals(1, violations.size());
        assertEquals(textFromValidation,
                "The description of the movie should be min 1 character, max 200 characters.");
    }

    @Test
    @DisplayName("A test to check for an incorrect release date.")
    void validateFilmReleaseDateInvalided() {

        filmDTO.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(filmDTO);
        String textFromValidation = getViolations(violations);

        assertEquals(1, violations.size());
        assertEquals(textFromValidation, "The release date of the film should not be earlier than 1895-12-28.");
    }

    @Test
    @DisplayName("A test to check the incorrect duration of the film.")
    void validateFilmDurationInvalided() {

        filmDTO.setDuration(-1);

        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(filmDTO);
        String textFromValidation = getViolations(violations);

        assertEquals(1, violations.size());
        assertEquals(textFromValidation, "The duration of the film should be positive.");
    }

    private String getViolations(Set<ConstraintViolation<FilmDTO>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(" "));
    }
}