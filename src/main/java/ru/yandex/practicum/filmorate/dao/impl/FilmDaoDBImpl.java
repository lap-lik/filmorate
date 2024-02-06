package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
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
    public static final String SAVE_FILM = "INSERT INTO films (name, description, release_date, duration, rate, mpa_id) " + "VALUES (?, ?, ?, ?, ?, ?)";
    public static final String FIND_FILMS = "SELECT f.*, m.name AS mpa_name, " + "(SELECT GROUP_CONCAT(fg.genre_id) FROM film_genre AS fg  WHERE fg.genre_id in (SELECT fg.genre_id " + "FROM film_genre AS fg WHERE (fg.film_id = f.id) ORDER BY fg.genre_id)) AS genre_id, " + "(SELECT GROUP_CONCAT(name) FROM GENRES WHERE id in (SELECT fg.genre_id " + "FROM film_genre AS fg WHERE (fg.film_id = f.id) ORDER BY fg.genre_id)) AS genre_name, " + "(SELECT GROUP_CONCAT(l.user_id) FROM likes AS l WHERE (l.film_id = f.id))  AS like_user_id " + "FROM films f LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.id";
    public static final String FIND_POPULAR_FILMS = FIND_FILMS + " WHERE f.id IN (SELECT * FROM (SELECT l.film_id " + "FROM likes AS l GROUP BY l.film_id ORDER BY COUNT(l.user_id) DESC " + "LIMIT ?) UNION (SELECT f.id FROM films AS f WHERE f.id NOT IN (SELECT l.film_id " + "FROM likes AS l GROUP BY l.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?) " + "LIMIT ? - (SELECT COUNT(*) FROM (SELECT l.film_id " + "FROM likes AS l GROUP BY l.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?))) limit ?)";
    public static final String FIND_FILM_BY_ID = FIND_FILMS + " WHERE f.id = ?";
    public static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? " + "WHERE id = ?";
    public static final String DELETE_FILM_BY_ID = "DELETE FROM films WHERE id = ?";
    public static final String IS_EXIST_FILM_BY_ID = "SELECT EXISTS (SELECT 1 FROM films WHERE id=?)";
    public static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) " + "SELECT ?, ? " + "WHERE NOT EXISTS (SELECT 1 FROM likes WHERE film_id = ? AND user_id = ?)";
    public static final String DELETE_LIKE = "DELETE FROM likes " + "WHERE film_id = ? AND user_id = ?";
    public static final String ADD_LINKS_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    public static final String DELETE_LINKS_FILM_GENRE = "DELETE FROM film_genre WHERE film_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {

        String sql = SAVE_FILM;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setObject(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId().intValue());
            return stmt;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        film.setId(filmId);
        addLinksFilmGenre(film);

        return findById(filmId).orElse(null);
    }

    @Override
    public Optional<Film> findById(Long filmId) {

        String sql = FIND_FILM_BY_ID;

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, filmId);

        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {

        String sql = FIND_FILMS;

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        return films.stream().sorted(Comparator.comparing(Film::getId)).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> update(Film film) {

        Long filmId = film.getId();
        String sql = UPDATE_FILM;

        int rowsUpdated = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), filmId);

        if (rowsUpdated == 0) {
            return Optional.empty();
        }

        addLinksFilmGenre(film);

        return findById(filmId);
    }

    @Override
    public boolean deleteById(Long filmId) {

        String sql = DELETE_FILM_BY_ID;

        int isFilmDelete = jdbcTemplate.update(sql, filmId);

        return isFilmDelete > 0;
    }

    @Override
    public boolean isExistsById(Long filmId) {

        String sql = IS_EXIST_FILM_BY_ID;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, filmId));
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        String sql = ADD_LIKE;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setInt(1, filmId.intValue());
            stmt.setInt(2, userId.intValue());
            stmt.setInt(3, filmId.intValue());
            stmt.setInt(4, userId.intValue());
            return stmt;
        }, keyHolder);

        try {
            Objects.requireNonNull(keyHolder.getKey()).longValue();
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public List<Film> findPopularFilms(int count) {

        String sql = FIND_POPULAR_FILMS;

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count, count, count, count, count);

        return films.stream().sorted(Comparator.comparingInt(f -> -f.getLikedUserIds().size())).collect(Collectors.toList());
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {

        String sql = DELETE_LIKE;

        int isLikeDeleted = jdbcTemplate.update(sql, filmId, userId);

        return isLikeDeleted > 0;
    }

    public void addLinksFilmGenre(Film film) {

        String sql = ADD_LINKS_FILM_GENRE;
        Long filmId = film.getId();
        Set<Genre> genres = film.getGenres();

        if (genres == null) {
            return;
        }

        deleteLinksFilmGenre(filmId);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
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

        String sql = DELETE_LINKS_FILM_GENRE;

        jdbcTemplate.update(sql, filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {


        String strGenresIds = resultSet.getString("genre_id");
        String strGenresNames = resultSet.getString("genre_name");

        String strLikes = resultSet.getString("like_user_id");
        Set<Long> usersIdsOfLikes = new TreeSet<>();
        if (strLikes != null) {
            String[] splitLikes = strLikes.split(",");
            usersIdsOfLikes.addAll(Arrays.stream(splitLikes).map(Long::valueOf).collect(Collectors.toList()));
        }

        return Film.builder().id(resultSet.getLong("id")).name(resultSet.getString("name")).description(resultSet.getString("description")).releaseDate(resultSet.getDate("release_date").toLocalDate()).duration(resultSet.getInt("duration")).rate((Integer) resultSet.getObject("rate")).mpa(mapRowToMpa(resultSet, 0)).genres(getGenres(strGenresIds, strGenresNames)).likedUserIds(new TreeSet<>(usersIdsOfLikes)).build();
    }

    private Set<Genre> getGenres(String strGenresIds, String strGenresNames) {
        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

        if (strGenresIds != null) {
            String[] splitGenresIds = strGenresIds.split(",");
            String[] splitGenresNames = strGenresNames.split(",");

            for (int i = 0; i < splitGenresIds.length; i++) {
                genres.add(Genre.builder().id(Long.valueOf(splitGenresIds[i])).name(splitGenresNames[i]).build());
            }
        }

        return genres;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {

        return Mpa.builder().id(resultSet.getLong("mpa_id")).name(resultSet.getString("mpa_name")).build();
    }
}
