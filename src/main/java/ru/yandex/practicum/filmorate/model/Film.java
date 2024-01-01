package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.anatation.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.constant.FilmConstant.RELEASE_DATA;

@Data
@Builder
public class Film {
    private Long id;

    @NotBlank(message = "The title of the film cannot be empty.")
    private String name;

    @Size(min = 1, max = 200, message = "The description of the movie should be min 1 character, max 200 characters.")
    private String description;

    @AfterDate(value = RELEASE_DATA, message = "The release date of the film should not be earlier than 1895-12-28.")
    private LocalDate releaseDate;

    @Positive(message = "The duration of the film should be positive.")
    private Integer duration;

    private final Set<Long> likes = new HashSet<>();
}