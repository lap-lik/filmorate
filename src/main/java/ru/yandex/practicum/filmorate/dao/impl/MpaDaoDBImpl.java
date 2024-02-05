package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
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
    public static final String SAVE_MPA = "INSERT INTO mpa (name) VALUES (?)";
    public static final String FIND_MPA_RATINGS = "SELECT * FROM mpa";
    public static final String UPDATE_MPA = "UPDATE mpa SET name = ? WHERE id = ?";
    public static final String DELETE_MPA_BY_ID = "DELETE FROM mpa WHERE id = ?";
    public static final String IS_EXIST_MPA_BY_ID = "SELECT EXISTS (SELECT 1 FROM mpa WHERE id=?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa save(Mpa mpa) {

        String sql = SAVE_MPA;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, mpa.getName());
            return stmt;
        }, keyHolder);

        Long mpaId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        mpa.setId(mpaId);

        return mpa;
    }

    @Override
    public Optional<Mpa> update(Mpa mpa) {

        Long mpaId = mpa.getId();
        String sql = UPDATE_MPA;

        int rowsUpdated = jdbcTemplate.update(sql, mpa.getName(), mpaId);

        if (rowsUpdated == 0) {
            return Optional.empty();
        }

        return Optional.of(mpa);
    }

    @Override
    public Optional<Mpa> findById(Long mpaId) {

        String sql = FIND_MPA_RATINGS + " WHERE id = ?";

        List<Mpa> mpaRatings = jdbcTemplate.query(sql, this::mapRowToMpa, mpaId);

        return mpaRatings.stream().findFirst();
    }

    @Override
    public List<Mpa> findAll() {

        String sql = FIND_MPA_RATINGS;

        List<Mpa> mpaRatings = jdbcTemplate.query(sql, this::mapRowToMpa);

        return mpaRatings.stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long mpaId) {

        String sql = DELETE_MPA_BY_ID;

        int isFilmDelete = jdbcTemplate.update(sql, mpaId);

        return isFilmDelete > 0;
    }

    @Override
    public boolean isExistsById(Long mpaId) {

        String sql = IS_EXIST_MPA_BY_ID;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, mpaId));
    }


    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {

        return Mpa.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
