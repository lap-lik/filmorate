package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.List;

import static ru.yandex.practicum.filmorate.constant.FilmConstant.COUNT_OF_POPULAR_FILM;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO createFilm(@RequestBody FilmDTO filmDTO) {

        log.info("START endpoint `method:POST /films` (create film), request: {}.", filmDTO);
        FilmDTO response = service.create(filmDTO);
        log.info("END endpoint `method:POST /films` (create film), response: {}.", response);

        return response;
    }

    @GetMapping("/{id}")
    public FilmDTO getFilmById(@PathVariable Long id) {

        log.info("START endpoint `method:GET /films/{id}` (get film by id), film id: {}.", id);
        FilmDTO response = service.getById(id);
        log.info("END endpoint `method:GET /films/{id}` (get film by id), response: {}.", response);

        return response;
    }

    @GetMapping
    public List<FilmDTO> getAllFilms() {

        log.info("START endpoint `method:GET /films` (get all films).");
        List<FilmDTO> response = service.getAll();
        log.info("END endpoint `method:GET /films` (get all films), response-size: {}.", response.size());

        return response;
    }

    @PutMapping
    public FilmDTO updateFilm(@RequestBody FilmDTO filmDTO) {

        log.info("START endpoint `method:PUT /films` (update film), request: {}.", filmDTO);
        FilmDTO response = service.update(filmDTO);
        log.info("END endpoint `method:PUT /films` (update film), response: {}.", response);

        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilmById(@PathVariable Long id) {

        log.info("START endpoint `method:DELETE /films/{id}` (delete film by id), film id: {}.", id);
        service.deleteById(id);
        log.info("END endpoint `method:DELETE /films/{id}` (delete film by id), response: HttpStatus.NO_CONTENT.");
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {

        log.info("START endpoint `method:PUT /films/{id}/like/{userId}` (add like to film), " +
                "film id: {}, user id: {}.", id, userId);
        service.likeFilm(id, userId);
        log.info("END endpoint `method:PUT /films/{id}/like/{userId}` (add like to film), response: HttpStatus.OK.");
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilms(@Positive(message = "must be greater than 0.") @RequestParam(defaultValue = COUNT_OF_POPULAR_FILM) String count) {

        log.info("START endpoint `method:GET /films/popular` (get must popular films), " +
                "count films: {}.", count);
        List<FilmDTO> response = service.getPopularFilms(count);
        log.info("END endpoint `method:GET /films/popular` (get must popular films), response-size: {}.", response.size());

        return response;
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("START endpoint `method:DELETE /films/{id}/like/{userId}` (delete like from film), " +
                "film id: {}, user id: {}.", id, userId);
        service.deleteLike(id, userId);
        log.info("END endpoint `method:DELETE /films/{id}/like/{userId}` (delete like from film) response: HttpStatus.NO_CONTENT.");
    }
}