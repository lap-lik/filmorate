package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exception.ValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The ValidatorUtils class provides utility methods for object validation using javax.validation.Validator.
 */
@Slf4j
public class ValidatorUtils {
    /**
     * Validates the given object using the default validator and specified groups.
     * If validation fails, throws ValidException with the validation error message.
     *
     * @param object The object to be validated.
     * @param groups The validator groups to be applied.
     * @param <T>    The type of the object to be validated.
     * @throws ValidException If validation fails.
     */
    public static <T> void validate(T object, Class<?>... groups) {

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (groups != null && groups.length > 0) {
            violations.addAll(validator.validate(object, groups));
        }

        if (!violations.isEmpty()) {
            throw ValidException.builder()
                    .message(buildValidationErrorMessage(violations))
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * Validates the user-name in the UserDTO object.
     * If the name is null or empty, sets it to the login value.
     *
     * @param userDTO The UserDTO object to validate.
     */
    public static void validateUserName(UserDTO userDTO) {

        String name = userDTO.getName();

        if (name == null || name.isEmpty()) {
            log.info("The user name must not be empty, the login value `{}` is used as the name.", userDTO.getLogin());
            userDTO.setName(userDTO.getLogin());
        }
    }

    /**
     * Builds the validation error message from the set of constraint violations.
     *
     * @param violations The set of constraint violations.
     * @param <T>        The type of the object being validated.
     * @return The validation error message.
     */
    private static <T> String buildValidationErrorMessage(Set<ConstraintViolation<T>> violations) {

        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" & "));
    }
}
