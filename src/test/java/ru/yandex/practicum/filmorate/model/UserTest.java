package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    private Validator validator;
    private final UserDTO userDTO = UserDTO.builder()
            .email("DarthVader@jedi.com")
            .login("DarthVader")
            .name("Anakin Skywalker")
            .birthday(LocalDate.of(2000, 1, 1))
            .build();

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    @DisplayName("A test to check an empty email.")
    void validateUserEmailEmpty() {

        userDTO.setEmail("");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        String textFromValidation = violations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(" "));

        assertEquals(2, violations.size());
        assertEquals(textFromValidation, "The email cannot be empty. The email is incorrect.");
    }

    @Test
    @DisplayName("A test to check the wrong email.")
    void validateUserEmailInvalided() {

        userDTO.setEmail("email.ru");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
        assertEquals(violations.iterator().next().getMessage(), "The email is incorrect.");
    }

    @Test
    @DisplayName("A test to verify an empty login.")
    void validateUserLoginEmpty() {

        userDTO.setLogin("");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        String textFromValidation = violations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(" "));

        assertEquals(2, violations.size());
        assertEquals(textFromValidation, "The login cannot be empty. The login must not contain spaces.");
    }

    @Test
    @DisplayName("A test to verify the login with spaces.")
    void validateUserLoginSpace() {

        userDTO.setLogin("do lore");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
        assertEquals(violations.iterator().next().getMessage(), "The login must not contain spaces.");
    }

    @Test
    @DisplayName("A test to check the wrong date of birth.")
    void validateUserBirthdayInvalided() {

        userDTO.setBirthday(LocalDate.of(2025, 1, 1));

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
        assertEquals(violations.iterator().next().getMessage(), "The date of birth cannot be in the future.");
    }
}