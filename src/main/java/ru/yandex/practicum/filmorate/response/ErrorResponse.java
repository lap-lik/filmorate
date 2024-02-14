package ru.yandex.practicum.filmorate.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ErrorResponse {

    private String message;
}
