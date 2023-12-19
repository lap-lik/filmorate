package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.FilmReleaseData;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;

    @FilmReleaseData
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;
}


