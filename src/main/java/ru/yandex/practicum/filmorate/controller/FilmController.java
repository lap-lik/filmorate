package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    @GetMapping
    public ResponseEntity<List<Film>> findAll() {
        log.info("Вызван список фильмов.");
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        Integer filmId = film.getId();
        if (!films.containsKey(filmId)) {
            log.error("Ошибка обновления: фильм с ID {} не найден.", filmId);
            return ResponseEntity.status(404).body(film);
        }
        films.put(filmId, film);
        log.info("Обновлен фильм: {}", film);
        return ResponseEntity.ok(film);
    }

    private Integer generateId() {
        return ++id;
    }
}
