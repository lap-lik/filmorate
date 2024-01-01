package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    private final User user = User.builder()
            .email("mail@mail.ru")
            .login("dolore")
            .name("Nick Name")
            .birthday(LocalDate.of(1946, 8, 20))
            .build();

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void validateUserEmailEmpty() {
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        String textFromValidation = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" "));
        assertEquals(2, violations.size());
        assertEquals("The email is incorrect. The email cannot be empty.", textFromValidation);
    }

    @Test
    void validateUserEmailInvalided() {
        user.setEmail("email.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("The email is incorrect.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginEmpty() {
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("The login cannot be empty.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginSpace() {
        user.setLogin("do lore");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("The login must not contain spaces.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserBirthdayInvalided() {
        user.setBirthday(LocalDate.of(2025, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("The date of birth cannot be in the future.", violations.iterator().next().getMessage());
    }
}