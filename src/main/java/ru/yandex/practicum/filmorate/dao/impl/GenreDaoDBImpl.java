package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
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
    public static final String SAVE_GENRE = "INSERT INTO genres (name) VALUES (?)";
    public static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?";
    public static final String FIND_ALL_GENRES = "SELECT * FROM genres";
    public static final String UPDATE_GENRE = "UPDATE genres SET name = ? WHERE id = ?";
    public static final String DELETE_GENRE_BY_ID = "DELETE FROM genres WHERE id = ?";
    public static final String IS_EXIST_GENRE_BY_ID = "SELECT EXISTS (SELECT 1 FROM genres WHERE id=?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre save(Genre genre) {

        String sql = SAVE_GENRE;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);

        Long genreId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        genre.setId(genreId);

        return genre;
    }

    @Override
    public Optional<Genre> update(Genre genre) {

        Long genreId = genre.getId();
        String sql = UPDATE_GENRE;

        int rowsUpdated = jdbcTemplate.update(sql, genre.getName(), genreId);

        if (rowsUpdated == 0) {
            return Optional.empty();
        }

        return Optional.of(genre);
    }

    @Override
    public Optional<Genre> findById(Long genreId) {

        String sql = FIND_GENRE_BY_ID;

        List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre, genreId);

        return genres.stream().findFirst();
    }

    @Override
    public List<Genre> findAll() {

        String sql = FIND_ALL_GENRES;

        List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre);

        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long genreId) {

        String sql = DELETE_GENRE_BY_ID;

        int isFilmDelete = jdbcTemplate.update(sql, genreId);

        return isFilmDelete > 0;
    }

    @Override
    public boolean isExistsById(Long genreId) {

        String sql = IS_EXIST_GENRE_BY_ID;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, genreId));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {

        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
