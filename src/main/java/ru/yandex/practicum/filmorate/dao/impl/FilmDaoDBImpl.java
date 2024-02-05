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
    public static final String SAVE_FILM = "INSERT INTO films (name, description, release_date, duration, rate, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String FIND_FILM = "SELECT f.*, m.name AS mpa_name, " +
            "(SELECT GROUP_CONCAT(CASE " +
            "WHEN f.id = fg.film_id THEN fg.genre_id END " +
            "ORDER BY  fg.genre_id  SEPARATOR ',') " +
            "FROM film_genre AS fg " +
            "WHERE (fg.film_id = f.id)) AS genre_id, " +
            "(SELECT GROUP_CONCAT(CASE " +
            "WHEN fg.genre_id = g.id THEN g.name END " +
            "ORDER BY g.name  SEPARATOR ',') " +
            "FROM genres AS g " +
            "LEFT OUTER JOIN film_genre AS fg ON g.id = fg.genre_id " +
            "WHERE (fg.film_id = f.id)) AS genre_name, " +
            "(SELECT GROUP_CONCAT(CASE " +
            "WHEN f.id = l.film_id THEN l.user_id END " +
            "ORDER BY  l.user_id  SEPARATOR ',') " +
            "FROM likes AS l " +
            "WHERE (l.film_id = f.id)) AS like_user_id " +
            "FROM films AS f " +
            "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.id";

    @Override
    public Optional<Film> findById(Long filmId) {

        String sql = FIND_FILM + " WHERE f.id = ?";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm1, filmId);

        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {

        String sql = FIND_FILM;

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm1);

        return films.stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> findPopularFilms(int count) {

        String sql = FIND_FILM + " WHERE f.id IN " +
                "(SELECT l.film_id FROM likes AS l group by l.film_id order by COUNT(l.user_id) DESC, l.film_id LIMIT ?)";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm1, count);

        return films.stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikedUserIds().size()))
                .collect(Collectors.toList());
    }


    private Film mapRowToFilm1(ResultSet resultSet, int rowNum) throws SQLException {


        String strGenresIds = resultSet.getString("genre_id");
        String strGenresNames = resultSet.getString("genre_name");
        List<Long> genresIds = new ArrayList<>();
        List<String> genresNames = new ArrayList<>();
        if (strGenresIds != null) {
            String[] splitGenresIds = strGenresIds.split(",");
            String[] splitGenresNames = strGenresNames.split(",");
            genresIds.addAll(Arrays.stream(splitGenresIds).map(Long::valueOf).collect(Collectors.toList()));
            genresNames = List.of(splitGenresNames);
        }

        String strLikes = resultSet.getString("like_user_id");
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
                .rate((Integer) resultSet.getObject("rate"))
                .mpa(mapRowToMpa1(resultSet, 0))
                .genres(getGenres(genresIds, genresNames))
                .likedUserIds(new TreeSet<>(usersIdsOfLikes))
                .build();
    }

    private Set<Genre> getGenres(List<Long> genresIds, List<String> genresNames) {
        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        if (!genresIds.isEmpty()) {
            for (int i = 0; i < genresIds.size(); i++) {
                genres.add(Genre.builder()
                        .id(genresIds.get(i))
                        .name(genresNames.get(i))
                        .build());
            }
        }
        return genres;
    }

    private Mpa mapRowToMpa1(ResultSet resultSet, int rowNum) throws SQLException {

        return Mpa.builder()
                .id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }


    public static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? " +
            "WHERE id = ?";
    public static final String DELETE_FILM_BY_ID = "DELETE FROM films WHERE id = ?";
    public static final String IS_EXIST_FILM_BY_ID = "SELECT EXISTS (SELECT 1 FROM films WHERE id=?)";
    public static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) " +
            "SELECT ?, ? " +
            "WHERE NOT EXISTS (SELECT 1 FROM likes WHERE film_id = ? AND user_id = ?)";
    public static final String FIND_MUST_POPULAR_FILMS = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, " +
            "g.name AS genre_name, l.user_id AS user_id " +
            "FROM films AS f " +
            "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.id " +
            "LEFT OUTER JOIN film_genre AS fg ON f.id = fg.film_id " +
            "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.id " +
            "LEFT OUTER JOIN likes AS l ON f.id = l.film_id " +
            "where f.id in " +
            "(SELECT l.film_id " +
            "FROM likes AS l " +
            "group by l.film_id " +
            "order by COUNT(l.user_id) DESC, l.film_id " +
            "LIMIT ?)";
    public static final String DELETE_LIKE = "DELETE FROM likes " +
            "WHERE film_id = ? AND user_id = ?";
    public static final String ADD_LINKS_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    public static final String DELETE_LINKS_FILM_GENRE = "DELETE FROM film_genre WHERE film_id = ?";
    public static final String FIND_USERS_ID_WHO_LIKED_FILM_BY_FILM_ID = "SELECT user_id " +
            "FROM likes " +
            "WHERE film_id = ?";
    public static final String FIND_GENRES_BY_FILM_ID = "SELECT id AS genre_id, name AS genre_name " +
            "FROM genres " +
            "WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";

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
        film.setGenres(findGenresByFilmId(filmId));
        film.setLikedUserIds(new HashSet<>());

        return film;
    }

    @Override
    public Optional<Film> update(Film film) {

        Long filmId = film.getId();
        String sql = UPDATE_FILM;

        int rowsUpdated = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().getId(), filmId);

        if (rowsUpdated == 0) {
            return Optional.empty();
        }

        addLinksFilmGenre(film);
        film.setGenres(findGenresByFilmId(filmId));
        film.setLikedUserIds(findLikesByFilmId(filmId));

        return Optional.of(film);
    }

//    @Override
//    public Optional<Film> findById(Long filmId) {
//
//        String sql = FIND_FILMS + " WHERE f.id = ?";
//        Map<Long, Film> filmMap = new HashMap<>();
//
//        jdbcTemplate.query(sql, resultSet -> {
//            Film film = getFilm(resultSet, filmMap);
//            filmMap.put(film.getId(), film);
//        }, filmId);
//
//        return filmMap.values().stream()
//                .findFirst();
//    }

//    @Override
//    public List<Film> findAll() {
//
//        String sql = FIND_FILMS;
//        Map<Long, Film> filmMap = new HashMap<>();
//
//        jdbcTemplate.query(sql, resultSet -> {
//            Film film = getFilm(resultSet, filmMap);
//            filmMap.put(film.getId(), film);
//        });
//
//        return filmMap.values().stream()
//                .sorted(Comparator.comparing(Film::getId))
//                .collect(Collectors.toList());
//    }

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

//    @Override
//    public List<Film> findPopularFilms(int count) {
//
//        String sql = FIND_MUST_POPULAR_FILMS;
//        Map<Long, Film> filmMap = new HashMap<>();
//
//        jdbcTemplate.query(sql, resultSet -> {
//            Film film = getFilm(resultSet, filmMap);
//            filmMap.put(film.getId(), film);
//        }, count);
//
//        return filmMap.values().stream()
//                .sorted(Comparator.comparingInt(f -> -f.getLikedUserIds().size()))
//                .collect(Collectors.toList());
//    }

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

    private Set<Long> findLikesByFilmId(Long filmId) {

        String sql = FIND_USERS_ID_WHO_LIKED_FILM_BY_FILM_ID;

        List<Long> resultList = jdbcTemplate.queryForList(sql, Long.class, filmId);

        return new HashSet<>(resultList);
    }

    private Set<Genre> findGenresByFilmId(Long filmId) {

        String sql = FIND_GENRES_BY_FILM_ID;

        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

        genres.addAll(jdbcTemplate.query(sql, this::mapRowToGenre, filmId));

        return genres;
    }

    private Film getFilm(ResultSet resultSet, Map<Long, Film> filmMap) throws SQLException {

        long id = resultSet.getLong("id");
        Film film = filmMap.get(id);

        if (film == null) {
            film = mapRowToFilm(resultSet, 0);
            film.setGenres(new TreeSet<>(Comparator.comparing(Genre::getId)));
            film.setLikedUserIds(new HashSet<>());
            filmMap.put(id, film);
        }

        Genre genre = mapRowToGenre(resultSet, 0);
        if (genre.getId() != 0) {
            film.getGenres().add(genre);
        }

        long userId = resultSet.getLong("user_id");
        if (userId != 0) {
            film.getLikedUserIds().add(userId);
        }

        return film;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate((Integer) resultSet.getObject("rate"))
                .mpa(mapRowToMpa(resultSet, 0))
                .build();
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {

        return Mpa.builder()
                .id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {

        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}
