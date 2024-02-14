package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.Marker;
import ru.yandex.practicum.filmorate.validation.anatation.AfterDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

import static ru.yandex.practicum.filmorate.constant.FilmConstant.RELEASE_DATA;

@Data
@Builder
public class FilmDTO {

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class, message = "The ID must not be empty.")
    private Long id;

    @NotBlank(message = "The name of the film must not be empty.")
    private String name;

    @Size(min = 1, max = 200, message = "The description of the movie should be min 1 character, max 200 characters.")
    private String description;

    @NotNull(message = "Release date must not not be null")
    @AfterDate(value = RELEASE_DATA, message = "The release date of the film should not be earlier than 1895-12-28.")
    private LocalDate releaseDate;

    @NotNull(message = "Duration must not not be null")
    @Positive(message = "The duration of the film should be positive.")
    private Integer duration;

    @NotNull(message = "The MPA rating of the film must not be empty.")
    private Mpa mpa;

    private Set<Long> likedUserIds;

    private Set<Genre> genres;
}
