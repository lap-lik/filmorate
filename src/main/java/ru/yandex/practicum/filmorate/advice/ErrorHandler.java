package ru.yandex.practicum.filmorate.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.advice.error.ErrorResponse;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidException;
import ru.yandex.practicum.filmorate.validator.RequestValidator;


@Slf4j
@RestControllerAdvice(assignableTypes = {RequestValidator.class, FilmController.class, UserController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validateErrorException(ValidException exception) {
        log.error("Exception: {}", exception.toString());
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(exception.getMessage()).build(), exception.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> badRequestException(BadRequestException exception) {
        log.error("Exception: {}", exception.toString());
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(exception.getMessage()).build(), exception.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException exception) {
        log.error("Exception: {}", exception.toString());
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(exception.getMessage()).build(), exception.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> throwableException(Exception exception) {
        log.error("Exception: {}", exception.toString());
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(exception.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
