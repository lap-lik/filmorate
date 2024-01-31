package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository(value = "filmMemory")
public class FilmDaoInMemoryImpl implements FilmDao {

    public final Map<Long, Film> FILMS = new HashMap<>();
    private Long id = 0L;

    @Override
    public Film save(Film newObject) {

        newObject.setId(generateId());
        FILMS.put(newObject.getId(), newObject);
        return newObject;
    }

    @Override
    public Optional<Film> findById(Long filmId) {

        return Optional.ofNullable(FILMS.get(filmId));
    }

    @Override
    public List<Film> findAll() {

        return new ArrayList<>(FILMS.values());
    }

    @Override
    public Optional<Film> update(Film updateObject) {
        Long id = updateObject.getId();

        if (FILMS.containsKey(id)) {
            FILMS.put(id, updateObject);
            return Optional.of(updateObject);
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long filmId) {

        return FILMS.remove(filmId) != null;
    }

    @Override
    public boolean isExistsById(Long filmId) {

        return FILMS.containsKey(filmId);
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {

        return FILMS.get(filmId).getLikedUserIds().add(userId);
    }

    @Override
    public List<Film> findPopularFilms(int count) {

        return FILMS.values().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikedUserIds().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {

        return FILMS.get(filmId).getLikedUserIds().remove(userId);
    }

    private Long generateId() {

        return ++id;
    }
}
