package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.SQLDataAccessException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository(value = "filmDB")
@RequiredArgsConstructor
public class FilmDaoDBImpl implements FilmDao {
    public static final String SAVE_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    public static final String FIND_FILMS = "SELECT f.*," +
            "       m.name                                                                        AS mpa_name," +
            "       (SELECT GROUP_CONCAT(genre_id) FROM film_genre AS fg WHERE fg.film_id = f.id) AS genre_ids," +
            "       (SELECT GROUP_CONCAT(name)" +
            "        FROM genres AS g" +
            "        WHERE g.id IN (SELECT fg.genre_id" +
            "                       FROM film_genre AS fg" +
            "                       WHERE fg.film_id = f.id" +
            "                       ORDER BY fg.genre_id))                                        AS genre_names," +
            "       (SELECT GROUP_CONCAT(l.user_id)" +
            "        FROM likes AS l" +
            "        WHERE (l.film_id = f.id))                                                    AS like_user_ids " +
            "FROM films f" +
            "         LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.id";
    public static final String FIND_POPULAR_FILMS = FIND_FILMS + " WHERE f.id IN (" +
            "    SELECT *" +
            "    FROM (SELECT l.film_id" +
            "          FROM likes AS l" +
            "          GROUP BY l.film_id" +
            "          ORDER BY COUNT(l.user_id) DESC" +
            "          LIMIT ?)" +
            "    UNION" +
            "    (SELECT f.id" +
            "     FROM films AS f" +
            "     WHERE f.id NOT IN (SELECT l.film_id" +
            "                        FROM likes AS l" +
            "                        GROUP BY l.film_id" +
            "                        ORDER BY COUNT(l.user_id) DESC" +
            "                        LIMIT ?)" +
            "     LIMIT ? - (SELECT COUNT(*)" +
            "                FROM (SELECT l.film_id" +
            "                      FROM likes AS l" +
            "                      GROUP BY l.film_id" +
            "                      ORDER BY COUNT(l.user_id) DESC" +
            "                      LIMIT ?)))" +
            "    limit ?)";
    public static final String FIND_FILM_BY_ID = FIND_FILMS +
            " WHERE f.id = ?";
    public static final String UPDATE_FILM = "UPDATE films " +
            "SET name         = ?," +
            "    description  = ?," +
            "    release_date = ?," +
            "    duration     = ?," +
            "    mpa_id       = ? " +
            "WHERE id = ?;";
    public static final String DELETE_FILM_BY_ID = "DELETE " +
            "FROM films " +
            "WHERE id = ?";
    public static final String IS_EXIST_FILM_BY_ID = "SELECT EXISTS (SELECT 1 FROM films WHERE id = ?)";
    public static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) " +
            "VALUES (?, ?)";
    public static final String DELETE_LIKE = "DELETE " +
            "FROM likes " +
            "WHERE film_id = ? " +
            "  AND user_id = ?";
    public static final String ADD_LINKS_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) " +
            "VALUES (?, ?)";
    public static final String DELETE_LINKS_FILM_GENRE = "DELETE " +
            "FROM film_genre " +
            "WHERE film_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SAVE_FILM, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId().intValue());
                return stmt;
            }, keyHolder);

            Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();

            film.setId(filmId);
            addLinksFilmGenre(film);

            return findById(filmId).orElse(null);
        } catch (DataAccessException exception) {
            throw new SQLDataAccessException("Error saving the film in the DB.", exception);
        }
    }

    @Override
    public Optional<Film> findById(Long filmId) {

        List<Film> films = jdbcTemplate.query(FIND_FILM_BY_ID, this::mapRowToFilm, filmId);

        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {

        List<Film> films = jdbcTemplate.query(FIND_FILMS, this::mapRowToFilm);

        return films.stream().sorted(Comparator.comparing(Film::getId)).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> update(Film film) {

        Long filmId = film.getId();
        try {
            int rowsUpdated = jdbcTemplate.update(UPDATE_FILM, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), filmId);

            if (rowsUpdated == 0) {
                return Optional.empty();
            }

            addLinksFilmGenre(film);

            return findById(filmId);
        } catch (DataAccessException exception) {
            throw new SQLDataAccessException("Error updating the film in the DB.", exception);
        }
    }

    @Override
    public boolean deleteById(Long filmId) {

        int filmDeleted = jdbcTemplate.update(DELETE_FILM_BY_ID, filmId);
        return filmDeleted > 0;
    }

    @Override
    public boolean isExistsById(Long filmId) {

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_FILM_BY_ID, Boolean.class, filmId));
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {

        try {
            jdbcTemplate.update(ADD_LIKE, filmId, userId);
            return true;
        } catch (DuplicateKeyException exception) {
            return false;
        }
    }

    @Override
    public List<Film> findPopularFilms(int count) {

        List<Film> films = jdbcTemplate.query(FIND_POPULAR_FILMS, this::mapRowToFilm, count, count, count, count, count);

        return films.stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikedUserIds().size()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {

        int likeDeleted = jdbcTemplate.update(DELETE_LIKE, filmId, userId);

        return likeDeleted > 0;
    }

    private void addLinksFilmGenre(Film film) {

        Long filmId = film.getId();
        Set<Genre> genres = film.getGenres();

        if (genres == null) {
            return;
        }

        deleteLinksFilmGenre(filmId);
        jdbcTemplate.batchUpdate(ADD_LINKS_FILM_GENRE, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {

                Genre genre = new ArrayList<>(genres).get(i);
                preparedStatement.setLong(1, film.getId());
                preparedStatement.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return film.getGenres().size();
            }
        });
    }

    private void deleteLinksFilmGenre(Long filmId) {

        jdbcTemplate.update(DELETE_LINKS_FILM_GENRE, filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        String strGenresIds = resultSet.getString("genre_ids");
        String strGenresNames = resultSet.getString("genre_names");

        String strLikes = resultSet.getString("like_user_ids");
        Set<Long> usersIdsOfLikes = new TreeSet<>();
        if (strLikes != null) {
            String[] splitLikes = strLikes.split(",");
            usersIdsOfLikes.addAll(Arrays.stream(splitLikes).map(Long::valueOf).collect(Collectors.toList()));
        }

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mapRowToMpa(resultSet))
                .genres(getGenres(strGenresIds, strGenresNames))
                .likedUserIds(new TreeSet<>(usersIdsOfLikes))
                .build();
    }

    private Set<Genre> getGenres(String strGenresIds, String strGenresNames) {

        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

        if (strGenresIds != null) {
            String[] splitGenresIds = strGenresIds.split(",");
            String[] splitGenresNames = strGenresNames.split(",");

            for (int i = 0; i < splitGenresIds.length; i++) {
                genres.add(Genre.builder()
                        .id(Long.valueOf(splitGenresIds[i]))
                        .name(splitGenresNames[i])
                        .build());
            }
        }

        return genres;
    }

    private Mpa mapRowToMpa(ResultSet resultSet) throws SQLException {

        return Mpa.builder()
                .id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }
}
