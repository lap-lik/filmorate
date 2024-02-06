package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository(value = "userDB")
@RequiredArgsConstructor
public class UserDaoDBImpl implements UserDao {
    public static final String SAVE_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String FIND_USERS = "SELECT u.*, " +
            "(SELECT GROUP_CONCAT(CASE " +
            "WHEN f.user_1 = u.id THEN f.user_2 " +
            "WHEN f.user_2 = u.id AND f.friendship_status = TRUE THEN f.user_1 END SEPARATOR ',') " +
            "FROM friendships f " +
            "WHERE (f.user_1 = u.id OR f.user_2 = u.id)) AS friends_ids " +
            "FROM users u";
    public static final String FIND_ALL_FRIENDS_BY_USER_ID = FIND_USERS + " WHERE u.id IN (SELECT user_1 " +
            "FROM friendships " +
            "WHERE user_2 = ? AND friendship_status = true " +
            "UNION " +
            "SELECT user_2 " +
            "FROM friendships " +
            "WHERE user_1 = ? )";
    public static final String FIND_COMMON_FRIENDS = FIND_USERS + " WHERE u.id IN (SELECT * " +
            "FROM (SELECT user_1 FROM friendships " +
            "WHERE user_2 = ? AND friendship_status = true " +
            "UNION " +
            "SELECT user_2 FROM friendships " +
            "WHERE user_1 = ?) " +
            "INTERSECT " +
            "(SELECT user_1 FROM friendships  " +
            "WHERE user_2 = ? AND friendship_status = true " +
            "UNION " +
            "SELECT user_2 FROM friendships WHERE user_1 = ?))";
    public static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    public static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    public static final String IS_EXIST_USER_BY_ID = "SELECT EXISTS (SELECT 1 FROM users WHERE id=?)";
    public static final String DELETE_FRIEND = "DELETE FROM friendships " +
            "WHERE (user_1 = ? AND user_2 = ?) OR (user_1 = ? AND user_2 = ?)";
    public static final String ADD_FRIEND = "INSERT INTO friendships (user_1, user_2) " +
            "SELECT ?, ? " +
            "WHERE NOT EXISTS (SELECT 1 FROM friendships WHERE user_1 = ? AND user_2 = ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {

        String sql = SAVE_USER;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findById(userId).orElse(null);
    }

    @Override
    public Optional<User> findById(Long userId) {

        String sql = FIND_USERS + " WHERE u.id = ?";

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, userId);

        return users.stream().findFirst();
    }

    @Override
    public List<User> findAll() {

        String sql = FIND_USERS;

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> update(User user) {

        Long userId = user.getId();
        String sql = UPDATE_USER;

        int rowsUpdated = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), userId);

        if (rowsUpdated == 0) {
            return Optional.empty();
        }

        return findById(userId);
    }

    @Override
    public boolean deleteById(Long userId) {

        String sql = DELETE_USER_BY_ID;

        int isFilmDelete = jdbcTemplate.update(sql, userId);

        return isFilmDelete > 0;
    }

    @Override
    public boolean isExistsById(Long userId) {

        String sql = IS_EXIST_USER_BY_ID;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId));
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {

        String sql = ADD_FRIEND;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setInt(1, userId.intValue());
            stmt.setInt(2, friendId.intValue());
            stmt.setInt(3, userId.intValue());
            stmt.setInt(4, friendId.intValue());
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
    public List<User> findAllFriends(Long userId) {

        String sql = FIND_ALL_FRIENDS_BY_USER_ID;

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, userId, userId);

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherId) {

        String sql = FIND_COMMON_FRIENDS;

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, userId, userId, otherId, otherId);

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {

        String sql = DELETE_FRIEND;

        int isFriendDeleted = jdbcTemplate.update(sql, userId, friendId, friendId, userId);

        return isFriendDeleted > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {

        String str = resultSet.getString("friends_ids");
        Set<Long> ids = new TreeSet<>();
        if (str != null) {
            String[] split = str.split(",");
            ids.addAll(Arrays.stream(split).map(Long::valueOf).collect(Collectors.toList()));
        }

        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(new TreeSet<>(ids))
                .build();
    }
}
