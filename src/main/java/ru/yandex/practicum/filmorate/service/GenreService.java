package ru.yandex.practicum.filmorate.service;


import ru.yandex.practicum.filmorate.dto.GenreDTO;

/**
 * The GenreService interface represents a service for managing genres.
 * It extends the GenericService interface with GenreDTO as the entity type and Long as the identifier type.
 *
 * @see GenericService
 */
public interface GenreService extends GenericService<GenreDTO, Long> {

}