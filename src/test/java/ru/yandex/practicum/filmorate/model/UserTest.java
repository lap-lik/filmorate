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
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserTest {
    private Validator validator;
    private User user = User.builder()
            .id(1)
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
        assertEquals(1, violations.size());
        assertEquals("Email не может быть пустым.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserEmailInvalided() {
        user.setEmail("email.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email должен содержать символ @.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginEmpty() {
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не может быть пустым.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginSpace() {
        user.setLogin("do lore");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не должен содержать пробелы.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserNameEmpty() {
        user.setName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
        assertFalse(user.getName().isEmpty(),
                "Имя пользователя не может быть пустым. Для имени должно быть использовано значение логина.");
        assertEquals(user.getName(), user.getLogin(),
                "Для пустого имени должно быть использовано значение логина.");
    }


    @Test
    void validateUserBirthdayInvalided() {
        user.setBirthday(LocalDate.of(2025, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем.", violations.iterator().next().getMessage());
    }
}