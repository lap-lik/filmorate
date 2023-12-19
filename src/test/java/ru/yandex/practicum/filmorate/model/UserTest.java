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
    public static final Integer ID = 1;
    public static final String EMAIL = "mail@mail.ru";
    public static final String LOGIN = "dolore";
    public static final String NAME = "Nick Name";
    public static final LocalDate BIRTHDAY = LocalDate.of(1946, 8, 20);
    public Validator validator;
    public User user;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void validateUserEmailEmpty() {
        user = User.builder()
                .id(ID)
                .email("")
                .login(LOGIN)
                .name(NAME)
                .birthday(BIRTHDAY)
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email не может быть пустым.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserEmailInvalided() {
        user = User.builder()
                .id(ID)
                .email("email.ru")
                .login(LOGIN)
                .name(NAME)
                .birthday(BIRTHDAY)
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email должен содержать символ @.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginEmpty() {
        user = User.builder()
                .id(ID)
                .email(EMAIL)
                .login("")
                .name(NAME)
                .birthday(BIRTHDAY)
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не может быть пустым.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLoginSpace() {
        user = User.builder()
                .id(ID)
                .email(EMAIL)
                .login("do lore")
                .name(NAME)
                .birthday(BIRTHDAY)
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не должен содержать пробелы.", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserNameEmpty() {
        user = User.builder()
                .id(ID)
                .email(EMAIL)
                .login(LOGIN)
                .name("")
                .birthday(BIRTHDAY)
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
        assertFalse(user.getName().isEmpty(),
                "Имя пользователя не может быть пустым. Для имени должно быть использовано значение логина.");
        assertEquals(user.getName(), user.getLogin(),
                "Для пустого имени должно быть использовано значение логина.");
    }


    @Test
    void validateUserBirthdayInvalided() {
        user = User.builder()
                .id(ID)
                .email(EMAIL)
                .login(LOGIN)
                .name(NAME)
                .birthday(LocalDate.of(2025, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем.", violations.iterator().next().getMessage());
    }
}