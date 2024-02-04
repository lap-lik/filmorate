package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.utils.ValidatorUtils;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDao genreDao;
    private final GenreMapper mapper;

    @Override
    public GenreDTO create(GenreDTO genreDTO) {

        ValidatorUtils.validate(genreDTO, Marker.OnCreate.class);

        return mapper.toDTO(genreDao.save(mapper.toEntity(genreDTO)));
    }

    @Override
    public GenreDTO getById(Long genreId) {

        return mapper.toDTO(genreDao.findById(genreId)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The genre with the ID - `%d` was not found.", genreId))
                        .httpStatus(NOT_FOUND)
                        .build()));
    }

    @Override
    public List<GenreDTO> getAll() {

        return mapper.toDTOs(genreDao.findAll());
    }

    @Override
    public GenreDTO update(GenreDTO genreDTO) {

        ValidatorUtils.validate(genreDTO, Marker.OnUpdate.class);

        return mapper.toDTO(genreDao.update(mapper.toEntity(genreDTO))
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The film `%s` was not found.", genreDTO.getName()))
                        .httpStatus(NOT_FOUND)
                        .build()));
    }

    @Override
    public void deleteById(Long genreId) {

        boolean isGenreDeleted = genreDao.deleteById(genreId);

        if (!isGenreDeleted) {
            throw NotFoundException.builder()
                    .message(String.format("The genre with the ID - `%d` was not found.", genreId))
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }
}
