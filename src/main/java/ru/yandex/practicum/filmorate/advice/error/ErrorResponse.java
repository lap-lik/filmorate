package ru.yandex.practicum.filmorate.advice.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String message;
}
