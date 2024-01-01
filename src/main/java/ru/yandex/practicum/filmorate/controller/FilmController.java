package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

import static ru.yandex.practicum.filmorate.constant.FilmConstant.COUNT_OF_POPULAR_FILM;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @PostMapping()
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFilm(film));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getFilmById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllFilms());
    }

    @PutMapping()
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateFilm(film));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Film> deleteFilmById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.deleteFilmById(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        service.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = COUNT_OF_POPULAR_FILM) String count) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getPopularFilms(count));
    }
}