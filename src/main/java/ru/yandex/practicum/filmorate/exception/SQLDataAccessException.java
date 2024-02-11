package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class SQLDataAccessException extends RuntimeException {

    public SQLDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}