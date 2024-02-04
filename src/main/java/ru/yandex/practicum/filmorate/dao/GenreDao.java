package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

/**
 * The GenreDao interface represents a data access object for managing genres.
 * It extends the GenericDao interface with Genre as the entity type and Long as the identifier type.
 * This interface uses standard CRUD methods inherited from the parent class.
 *
 * @see GenericDao
 */
public interface GenreDao extends GenericDao<Genre, Long> {

}
