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

        log.info("START endpoint `method:POST /genres` (create genre), request: {}.", genreDTO.getName());

        return service.create(genreDTO);
    }

    @GetMapping("/{id}")
    public GenreDTO getGenreById(@PathVariable Long id) {

        log.info("START endpoint `method:GET /genres/{id}` (get genre by id), genre id: {}.", id);

        return service.getById(id);
    }

    @GetMapping
    public List<GenreDTO> getAllGenres() {

        log.info("START endpoint `method:GET /genres` (get all genres).");

        return service.getAll();
    }

    @PutMapping
    public GenreDTO updateGenre(@RequestBody GenreDTO genreDTO) {

        log.info("START endpoint `method:PUT /genres` (update genre), request: {}.", genreDTO.getName());

        return service.update(genreDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenreById(@PathVariable Long id) {

        log.info("START endpoint `method:DELETE /genres/{id}` (delete genre by id), genre id: {}.", id);
        service.deleteById(id);
    }
}