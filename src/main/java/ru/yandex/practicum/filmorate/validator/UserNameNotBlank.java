package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UserNameNotBlankValidator.class)
public @interface UserNameNotBlank {
    String message() default "Имя пользователя не может быть пустым. Для имени использовано значение логина.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
