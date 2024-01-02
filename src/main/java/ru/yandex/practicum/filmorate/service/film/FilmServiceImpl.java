package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.RequestValidator;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.yandex.practicum.filmorate.constant.FilmConstant.COUNT_OF_POPULAR_FILM;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;
    private final RequestValidator requestValidator;

    @Override
    public Film createFilm(Film newFilm) {
        log.info("Request to create a film: ID - `{}` Title - `{}`", newFilm.getId(), newFilm.getName());
        requestValidator.validationRequest(newFilm);
        Film film = filmDao.saveFilm(newFilm);
        log.info("The film has been created: ID - `{}` Title - `{}`.", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        log.info("Request to receive a film by ID - `{}`", filmId);
        Film film = filmDao.findFilmById(filmId)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The film with the ID - `%d` was not found.", filmId))
                        .httpStatus(NOT_FOUND).build());
        log.info("A film has been received: ID - `{}` Title - `{}`.", film.getId(), film.getName());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Request for a list of films.");
        List<Film> films = filmDao.findAllFilms();
        if (films.isEmpty()) {
            log.warn("The list of films is empty.");
        } else {
            log.info("The list of films has been received.");
        }
        return films;
    }

    @Override
    public Film updateFilm(Film updateObject) {
        log.info("Film update request ID - `{}` Title - `{}`.", updateObject.getId(), updateObject.getName());
        requestValidator.validationRequest(updateObject);
        Film film = filmDao.updateFilm(updateObject)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The film `%s` was not found.", updateObject.getName()))
                        .httpStatus(NOT_FOUND).build());
        log.info("Film has been updated: ID - `{}` Title - `{}`.", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film deleteFilmById(Long filmId) {
        log.info("Request was received to delete a film with the ID - `{}`.", filmId);
        Film film = filmDao.removeFilmById(filmId)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The film with the ID - `%d` was not found.", filmId))
                        .httpStatus(NOT_FOUND).build());
        log.info("The film has been deleted: ID - `{}` Title - `{}`.", film.getId(), film.getName());
        return film;
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        log.info("Request was received to add a like to a film with ID - `{}`, by a user with ID - `{}`.",
                filmId, userId);
        checkIds(filmId, userId);
        boolean isLiked = filmDao.addLikeToFilm(filmId, userId);
        if (!isLiked) {
            throw BadRequestException.builder()
                    .message(String.format("The user with the ID - `%d` has already liked the film with the ID - `%d`.",
                            userId, filmId))
                    .httpStatus(BAD_REQUEST).build();
        }
        log.info("Like was added to the film with the ID - `{}`.", filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        log.info("Request was received to remove the like from the film by ID - `{}`, a user with ID - `{}`.",
                filmId, userId);
        checkIds(filmId, userId);
        boolean isLiked = filmDao.removeLikeFromFilm(filmId, userId);
        if (!isLiked) {
            throw BadRequestException.builder()
                    .message(String.format("The user with the ID - `%d` did not like the film with the ID - `%d`.",
                            userId, filmId))
                    .httpStatus(BAD_REQUEST).build();
        }
        log.info("The like was removed from the film with the ID - `{}`.", filmId);
    }

    @Override
    public List<Film> getPopularFilms(String count) {
        int numberFilms = checkCount(count);
        List<Film> films = filmDao.findPopularFilms(numberFilms);
        if (films.isEmpty()) {
            log.warn("The list of best films is empty.");
        } else {
            log.info("A list of the {} most popular films has been received.", numberFilms);
        }
        return films;
    }

    private int checkCount(String count) {
        try {
            int numberFilms = Integer.parseInt(count);
            log.info("Request to receive the `{}` most popular films.", count);
            return numberFilms;
        } catch (NumberFormatException e) {
            log.warn("Incorrect request for the number - `{}` of popular films, the default value is used - `{}`.",
                    count, COUNT_OF_POPULAR_FILM);
            return Integer.parseInt(COUNT_OF_POPULAR_FILM);
        }
    }

    private void checkIds(Long filmId, Long userId) {
        StringBuilder messages = new StringBuilder();
        boolean filmExists = filmDao.existsFilmById(filmId);
        boolean userExists = userDao.existsUserById(userId);

        if (!filmExists) {
            messages.append(String.format("Film by ID - `%d` has not been found.", filmId));
        }
        if (!userExists) {
            messages.append(String.format("User by ID - `%d` has not been found.", userId));
        }
        if (!filmExists || !userExists) {
            log.error("Error {}", messages);
            throw NotFoundException.builder()
                    .message(messages.toString())
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }
}
