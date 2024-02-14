package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class GenreDTO {

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class, message = "The ID must not be empty.")
    private Long id;

    @NotBlank(message = "The name of the genre must not be empty.")
    private String name;
}