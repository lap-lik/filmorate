package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository(value = "filmDB")
@RequiredArgsConstructor
public class FilmDaoDBImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLike(Long filmId, Long userId) {
        return false;
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        return null;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        return false;
    }

//    @Override
//    public Optional<Film> findById(Long filmId) {
//
//        String selectQuery = "SELECT * FROM films WHERE id = ?";
//
//        return Optional.ofNullable(jdbcTemplate.queryForObject(selectQuery, new Object[]{filmId},
//                 BeanPropertyRowMapper.newInstance(Film.class)));
//    }

    @Override
    public Optional<Film> findById(Long filmId) {

        String selectQuery = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, l.user_id AS user_id " +
                "FROM films AS f " +
                "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT OUTER JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.id " +
                "LEFT OUTER JOIN likes AS l ON f.id = l.film_id " +
                "WHERE f.id = ?";

        return Optional.ofNullable(jdbcTemplate.queryForObject(selectQuery, this::mapRowToFilm, filmId));
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpaRating(getMpaFromSelect(resultSet))
                .build();
        film.getLikedUserIds().addAll(getLikesFromSelect(resultSet));
        film.getGenres().addAll(getGenresFromSelect(resultSet));
        return film;
    }

    private Mpa getMpaFromSelect(ResultSet resultSet) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }

    private List<Long> getLikesFromSelect(ResultSet resultSet) throws SQLException {
        List<Long> likes = new ArrayList<>();
        while (resultSet.next()) {
            Long userId = resultSet.getLong("user_id");
            if (userId != null) {
                likes.add(userId);
            }
        }
        return likes;
    }

    private List<Genre> getGenresFromSelect(ResultSet resultSet) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        while (resultSet.next()){
            Long genreId = resultSet.getLong("genre_id");
            if (genreId != null){
                Genre genre = Genre.builder()
                        .id(genreId)
                        .name(resultSet.getString("genre_name"))
                        .build();
                genres.add(genre);
            }
        }
        return genres;
    }

    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film save(Film entity) {
        return null;
    }

    @Override
    public Optional<Film> update(Film entity) {
        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long aLong) {
        return false;
    }

    @Override
    public boolean isExistsById(Long aLong) {

        String selectQuery = "SELECT EXISTS (SELECT 1 FROM films WHERE id=?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(selectQuery, Boolean.class, aLong));
    }
}
