package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film newObject);

    Film getFilmById(Long filmId);

    List<Film> getAllFilms();

    Film updateFilm(Film updateObject);

    Film deleteFilmById(Long filmId);

    void likeFilm(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopularFilms(String count);
}
