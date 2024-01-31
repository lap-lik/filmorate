package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.utils.ValidatorUtils;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.yandex.practicum.filmorate.constant.FilmConstant.FILM_DAO_IMPL;
import static ru.yandex.practicum.filmorate.constant.UserConstant.USER_DAO_IMPL;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmDao filmDao;
    private final UserDao userDao;
    private final FilmMapper mapper;

    @Autowired
    public FilmServiceImpl(@Qualifier(value = FILM_DAO_IMPL) FilmDao filmDao,
                           @Qualifier(value = USER_DAO_IMPL) UserDao userDao,
                           FilmMapper mapper) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.mapper = mapper;
    }

    @Override
    public FilmDTO create(FilmDTO filmDTO) {

        ValidatorUtils.validate(filmDTO, Marker.OnCreate.class);

        return mapper.toDTO(filmDao.save(mapper.toEntity(filmDTO)));
    }

    @Override
    public FilmDTO getById(Long filmId) {

        return mapper.toDTO(filmDao.findById(filmId)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The film with the ID - `%d` was not found.", filmId))
                        .httpStatus(NOT_FOUND)
                        .build()));
    }

    @Override
    public List<FilmDTO> getAll() {

        return mapper.toDTOs(filmDao.findAll());
    }

    @Override
    public FilmDTO update(FilmDTO filmDTO) {

        ValidatorUtils.validate(filmDTO, Marker.OnUpdate.class);

        return mapper.toDTO(filmDao.update(mapper.toEntity(filmDTO))
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The film `%s` was not found.", filmDTO.getName()))
                        .httpStatus(NOT_FOUND)
                        .build()));
    }

    @Override
    public void deleteById(Long filmId) {

        boolean isFilmDeleted = filmDao.deleteById(filmId);

        if (!isFilmDeleted) {
            throw NotFoundException.builder()
                    .message(String.format("The film with the ID - `%d` was not found.", filmId))
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {

        checkIds(filmId, userId);
        boolean isAddedLike = filmDao.addLike(filmId, userId);

        if (!isAddedLike) {
            throw BadRequestException.builder()
                    .message(String.format("The user with the ID - `%d` has already liked the film with the ID - `%d`.",
                            userId, filmId))
                    .httpStatus(BAD_REQUEST)
                    .build();
        }
    }

    @Override
    public List<FilmDTO> getPopularFilms(String count) {

        return mapper.toDTOs(filmDao.findPopularFilms(Integer.parseInt(count)));
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

        checkIds(filmId, userId);
        boolean isDeletedLike = filmDao.removeLike(filmId, userId);

        if (!isDeletedLike) {
            throw BadRequestException.builder()
                    .message(String.format("The user with the ID - `%d` did not like the film with the ID - `%d`.",
                            userId, filmId))
                    .httpStatus(BAD_REQUEST)
                    .build();
        }
    }

    private void checkIds(Long filmId, Long userId) {

        List<String> messages = new ArrayList<>();
        boolean filmExists = filmDao.isExistsById(filmId);
        boolean userExists = userDao.isExistsById(userId);

        if (!filmExists) {
            messages.add(String.format("The film by ID - `%d` was not found.", filmId));
        }

        if (!userExists) {
            messages.add(String.format("The user by ID - `%d` was not found.", userId));
        }

        if (!messages.isEmpty()) {
            throw NotFoundException.builder()
                    .message(String.join(" & ", messages))
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }
}
