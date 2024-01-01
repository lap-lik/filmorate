package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDaoImpl implements FilmDao {
    private static final Map<Long, Film> FILMS = new HashMap<>();
    private Long id = 0L;

    @Override
    public Film saveFilm(Film newObject) {
        newObject.setId(generateId());
        FILMS.put(newObject.getId(), newObject);
        return newObject;
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        return Optional.ofNullable(FILMS.get(filmId));
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(FILMS.values());
    }

    @Override
    public Optional<Film> updateFilm(Film updateObject) {
        Long id = updateObject.getId();
        if (FILMS.containsKey(id)) {
            FILMS.put(id, updateObject);
            return Optional.of(updateObject);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Film> removeFilmById(Long filmId) {
        return Optional.ofNullable(FILMS.remove(filmId));
    }

    @Override
    public boolean existsFilmById(Long filmId) {
        return FILMS.containsKey(filmId);
    }

    @Override
    public boolean addLikeToFilm(Long filmId, Long userId) {
        return FILMS.get(filmId).getLikes().add(userId);
    }

    @Override
    public boolean removeLikeFromFilm(Long filmId, Long userId) {
        return FILMS.get(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        return FILMS.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }


    private Long generateId() {
        return ++id;
    }
}
