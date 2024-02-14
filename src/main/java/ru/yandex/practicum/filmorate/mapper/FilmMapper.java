package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * The FilmMapper interface represents a mapper for converting Film entities to FilmDTOs and vice versa.
 *
 * @see GenericMapper
 */
@Mapper(componentModel = "spring")
public interface FilmMapper extends GenericMapper<Film, FilmDTO> {
}
