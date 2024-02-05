package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDTO createGenre(@RequestBody GenreDTO genreDTO) {

        log.info("START endpoint `method:POST /genres` (create genre), request: {}.", genreDTO);
        GenreDTO response = service.create(genreDTO);
        log.info("END endpoint `method:POST /genres` (create genre), response: {}.", response);

        return response;
    }

    @GetMapping("/{id}")
    public GenreDTO getGenreById(@PathVariable Long id) {

        log.info("START endpoint `method:GET /genres/{id}` (get genre by id), genre id: {}.", id);
        GenreDTO response = service.getById(id);
        log.info("END endpoint `method:GET /genres/{id}` (get genre by id), response: {}.", response);

        return response;
    }

    @GetMapping
    public List<GenreDTO> getAllGenres() {

        log.info("START endpoint `method:GET /genres` (get all genres).");
        List<GenreDTO> response = service.getAll();
        log.info("END endpoint `method:GET /genres` (get all genres), response-size: {}.", response.size());

        return response;
    }

    @PutMapping
    public GenreDTO updateGenre(@RequestBody GenreDTO genreDTO) {

        log.info("START endpoint `method:PUT /genres` (update genre), request: {}.", genreDTO);
        GenreDTO response = service.update(genreDTO);
        log.info("END endpoint `method:PUT /genres` (update genre), response: {}.", response);

        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenreById(@PathVariable Long id) {

        log.info("START endpoint `method:DELETE /genres/{id}` (delete genre by id), genre id: {}.", id);
        service.deleteById(id);
        log.info("END endpoint `method:DELETE /genres/{id}` (delete genre by id), response: HttpStatus.NO_CONTENT.");
    }

}