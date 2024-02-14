package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * The GenreMapper interface represents a mapper for converting Genre entities to GenreDTOs and vice versa.
 *
 * @see GenericMapper
 */
@Mapper(componentModel = "spring")
public interface GenreMapper extends GenericMapper<Genre, GenreDTO> {
}