package ru.yandex.practicum.filmorate.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class BadRequestException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;
}
