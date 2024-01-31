package ru.yandex.practicum.filmorate.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ValidException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;
}
