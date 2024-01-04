package ru.yandex.practicum.filmorate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidator {
    private final Validator validator;

    public final <T> void validationRequest(T request) {
        if (request != null) {
            Set<ConstraintViolation<T>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                String validations = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(" "));
                log.error("Validation errors: {}", validations);
                throw ValidException.builder()
                        .message(validations)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }
        }
    }
}
