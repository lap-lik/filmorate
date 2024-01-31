package ru.yandex.practicum.filmorate.validation.anatation;

import ru.yandex.practicum.filmorate.validation.AfterDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom annotation for checking the input data for the date after the set or default value.
 */
@Documented
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = AfterDateValidator.class)
public @interface AfterDate {
    String message() default "The date cannot be earlier {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value() default "1970-01-01";
}