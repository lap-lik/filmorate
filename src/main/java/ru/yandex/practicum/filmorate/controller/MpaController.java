package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MpaDTO createMpa(@RequestBody MpaDTO mpaDTO) {

        log.info("START endpoint `method:POST /mpa` (create MPA rating), request: {}.", mpaDTO.getName());

        return service.create(mpaDTO);
    }

    @GetMapping("/{id}")
    public MpaDTO getMpaById(@PathVariable Long id) {

        log.info("START endpoint `method:GET /mpa/{id}` (get MPA rating by id), MPA rating id: {}.", id);

        return service.getById(id);
    }

    @GetMapping
    public List<MpaDTO> getAllMpaRatings() {

        log.info("START endpoint `method:GET /mpa` (get all MPA ratings).");

        return service.getAll();
    }

    @PutMapping
    public MpaDTO updateMpa(@RequestBody MpaDTO mpaDTO) {

        log.info("START endpoint `method:PUT /mpa` (update MPA rating), request: {}.", mpaDTO.getName());

        return service.update(mpaDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMpaById(@PathVariable Long id) {

        log.info("START endpoint `method:DELETE /mpa/{id}` (delete MPA rating by id), MPA rating id: {}.", id);
        service.deleteById(id);
    }
}