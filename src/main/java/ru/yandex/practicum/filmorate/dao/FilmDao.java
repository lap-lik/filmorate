package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * The FilmDao interface represents a data access object for managing films.
 * It extends the GenericDao interface with Film as the entity type and Long as the identifier type.
 * The methods provided in this interface allow adding likes to film, retrieving popular films, and removing likes.
 *
 * @see GenericDao
 */
public interface FilmDao extends GenericDao<Film, Long> {

    /**
     * Likes a film with the specified film ID and user ID.
     *
     * @param filmId The ID of the film to be liked.
     * @param userId The ID of the user who likes the film.
     * @return true if the like is added successfully, false otherwise.
     */
    boolean addLike(final Long filmId, final Long userId);

    /**
     * Retrieves a list of popular films.
     *
     * @param count The number of popular films to retrieve.
     * @return A list of popular Film objects.
     */
    List<Film> findPopularFilms(int count);

    /**
     * Removes a like from a film with the specified film ID and user ID.
     *
     * @param filmId The ID of the film to be unliked.
     * @param userId The ID of the user who unlikes the film.
     * @return true if the like is removed successfully, false otherwise.
     */
    boolean deleteLike(final Long filmId, final Long userId);
}
