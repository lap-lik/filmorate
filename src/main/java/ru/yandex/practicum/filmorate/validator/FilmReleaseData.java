package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = FilmReleaseDataValidator.class)

public @interface FilmReleaseData {
    String message() default "Дата релиза фильма не должна быть раньше 28 декабря 1895 года.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
