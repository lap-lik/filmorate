package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Film saveFilm(Film newObject);

    Optional<Film> findFilmById(final Long filmId);

    List<Film> findAllFilms();

    Optional<Film> updateFilm(Film updateObject);

    Optional<Film> removeFilmById(final Long filmId);

    boolean existsFilmById(final Long filmId);

    boolean addLikeToFilm(final Long filmId, final Long userId);

    boolean removeLikeFromFilm(final Long filmId, final Long userId);

    List<Film> findPopularFilms(int count);
}
