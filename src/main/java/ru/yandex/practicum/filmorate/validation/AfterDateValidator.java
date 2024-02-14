package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.validation.anatation.AfterDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.constant.FilmConstant.FORMATTER;

/**
 * The AfterDateValidator class is a validator implementation for the AfterDate constraint.
 * It checks if a given LocalDate is after a specified date.
 */
public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {

    /**
     * The date before which the input date should not be.
     */
    private LocalDate beforeDate;

    /**
     * Sets the value of the field relative to which the input data is being checked.
     *
     * @param constraintAnnotation Interface for initializing set or default data.
     */
    @Override
    public void initialize(AfterDate constraintAnnotation) {
        beforeDate = LocalDate.parse(constraintAnnotation.value(), FORMATTER);
    }

    /**
     * Checks if the input date is after the specified date.
     *
     * @param releaseDate                The input date to be validated.
     * @param constraintValidatorContext The constraint validator context.
     * @return Returns true if the input date is after the specified date, false otherwise.
     */
    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return !beforeDate.isAfter(releaseDate);
    }
}
