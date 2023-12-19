package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.constant.Constants.RELEASE_DATA;

public class FilmReleaseDataValidator implements ConstraintValidator<FilmReleaseData, LocalDate> {
    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate.isAfter(RELEASE_DATA);
    }
}
