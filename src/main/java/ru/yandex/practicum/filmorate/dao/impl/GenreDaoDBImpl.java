package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.SQLDataAccessException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDaoDBImpl implements GenreDao {
    public static final String SAVE_GENRE = "INSERT INTO genres (name) " +
            "VALUES (?)";
    public static final String FIND_GENRES = "SELECT * " +
            "FROM genres";
    public static final String FIND_GENRES_BY_ID = FIND_GENRES + " WHERE id = ?";
    public static final String UPDATE_GENRE = "UPDATE genres " +
            "SET name = ? " +
            "WHERE id = ?";
    public static final String DELETE_GENRE_BY_ID = "DELETE " +
            "FROM genres " +
            "WHERE id = ?";
    public static final String IS_EXIST_GENRE_BY_ID = "SELECT EXISTS (SELECT 1 FROM genres WHERE id = ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre save(Genre genre) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SAVE_GENRE, new String[]{"id"});
                stmt.setString(1, genre.getName());
                return stmt;
            }, keyHolder);

            Long genreId = Objects.requireNonNull(keyHolder.getKey()).longValue();

            genre.setId(genreId);

            return genre;
        } catch (DataAccessException exception) {
            throw new SQLDataAccessException("Error saving the genre in the DB.", exception);
        }
    }

    @Override
    public Optional<Genre> update(Genre genre) {

        Long genreId = genre.getId();
        try {
            int rowsUpdated = jdbcTemplate.update(UPDATE_GENRE, genre.getName(), genreId);

            if (rowsUpdated == 0) {
                return Optional.empty();
            }

            return Optional.of(genre);
        } catch (
                DataAccessException exception) {
            throw new SQLDataAccessException("Error updating the genre in the DB.", exception);
        }
    }

    @Override
    public Optional<Genre> findById(Long genreId) {

        List<Genre> genres = jdbcTemplate.query(FIND_GENRES_BY_ID, this::mapRowToGenre, genreId);

        return genres.stream().findFirst();
    }

    @Override
    public List<Genre> findAll() {

        List<Genre> genres = jdbcTemplate.query(FIND_GENRES, this::mapRowToGenre);

        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long genreId) {

        int genreDeleted = jdbcTemplate.update(DELETE_GENRE_BY_ID, genreId);

        return genreDeleted > 0;
    }

    @Override
    public boolean isExistsById(Long genreId) {

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_GENRE_BY_ID, Boolean.class, genreId));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {

        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
