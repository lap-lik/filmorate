package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.SQLDataAccessException;
import ru.yandex.practicum.filmorate.model.Mpa;

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
public class MpaDaoDBImpl implements MpaDao {
    public static final String SAVE_MPA = "INSERT INTO mpa (name) " +
            "VALUES (?)";
    public static final String FIND_MPA_RATINGS = "SELECT * " +
            "FROM mpa";
    public static final String FIND_MPA_RATINGS_BY_ID = FIND_MPA_RATINGS + " WHERE id = ?";
    public static final String UPDATE_MPA = "UPDATE mpa " +
            "SET name = ? " +
            "WHERE id = ?";
    public static final String DELETE_MPA_BY_ID = "DELETE " +
            "FROM mpa " +
            "WHERE id = ?";
    public static final String IS_EXIST_MPA_BY_ID = "SELECT EXISTS (SELECT 1 FROM mpa WHERE id = ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa save(Mpa mpa) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SAVE_MPA, new String[]{"id"});
                stmt.setString(1, mpa.getName());
                return stmt;
            }, keyHolder);

        Long mpaId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        mpa.setId(mpaId);

        return mpa;
        } catch (
                DataAccessException exception) {
            throw new SQLDataAccessException("Error saving the mpa in the DB.", exception);
        }
    }

    @Override
    public Optional<Mpa> update(Mpa mpa) {

        Long mpaId = mpa.getId();
        try {
            int rowsUpdated = jdbcTemplate.update(UPDATE_MPA, mpa.getName(), mpaId);

            if (rowsUpdated == 0) {
                return Optional.empty();
            }

            return Optional.of(mpa);
        } catch (
                DataAccessException exception) {
            throw new SQLDataAccessException("Error updating the mpa in the DB.", exception);
        }
    }

    @Override
    public Optional<Mpa> findById(Long mpaId) {

        List<Mpa> mpaRatings = jdbcTemplate.query(FIND_MPA_RATINGS_BY_ID, this::mapRowToMpa, mpaId);

        return mpaRatings.stream().findFirst();
    }

    @Override
    public List<Mpa> findAll() {

        List<Mpa> mpaRatings = jdbcTemplate.query(FIND_MPA_RATINGS, this::mapRowToMpa);

        return mpaRatings.stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long mpaId) {

        int mpaDeleted = jdbcTemplate.update(DELETE_MPA_BY_ID, mpaId);

        return mpaDeleted > 0;
    }

    @Override
    public boolean isExistsById(Long mpaId) {

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_MPA_BY_ID, Boolean.class, mpaId));
    }


    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {

        return Mpa.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
