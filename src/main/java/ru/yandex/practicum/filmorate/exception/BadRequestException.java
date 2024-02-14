package ru.yandex.practicum.filmorate.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class BadRequestException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;
}
