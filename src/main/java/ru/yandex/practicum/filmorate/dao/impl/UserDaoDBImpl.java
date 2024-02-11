package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.SQLDataAccessException;
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
    public static final String FIND_USER_BY_ID = FIND_USERS + " WHERE u.id = ?";
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
    public static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    public static final String DELETE_USER_BY_ID = "DELETE FROM users " +
            "WHERE id = ?";
    public static final String IS_EXIST_USER_BY_ID = "SELECT EXISTS (SELECT 1 FROM users WHERE id=?)";
    public static final String ADD_FRIEND = "INSERT INTO friendships (user_1, user_2) " +
            "SELECT ?, ? FROM DUAL " +
            "WHERE NOT EXISTS (" +
            "SELECT 1 " +
            "FROM friendships " +
            "WHERE (user_1 = ? AND user_2 = ?) OR (user_1 = ? AND user_2 = ?))";
    public static final String DELETE_FRIEND = "DELETE FROM friendships " +
            "WHERE (user_1 = ? AND user_2 = ?) OR (user_1 = ? AND user_2 = ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SAVE_USER, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);

            Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();

            user.setId(userId);

            return findById(userId).orElse(null);
        } catch (
                DataAccessException exception) {
            throw new SQLDataAccessException("Error saving the user in the DB.", exception);
        }
    }

    @Override
    public Optional<User> findById(Long userId) {

        List<User> users = jdbcTemplate.query(FIND_USER_BY_ID, this::mapRowToUser, userId);

        return users.stream().findFirst();
    }

    @Override
    public List<User> findAll() {

        List<User> users = jdbcTemplate.query(FIND_USERS, this::mapRowToUser);

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> update(User user) {

        Long userId = user.getId();
        try {
            int rowsUpdated = jdbcTemplate.update(UPDATE_USER, user.getEmail(), user.getLogin(), user.getName(),
                    user.getBirthday(), userId);

            if (rowsUpdated == 0) {
                return Optional.empty();
            }

            return findById(userId);
        } catch (
                DataAccessException exception) {
            throw new SQLDataAccessException("Error updating the user in the DB.", exception);
        }
    }

    @Override
    public boolean deleteById(Long userId) {

        int isUserDelete = jdbcTemplate.update(DELETE_USER_BY_ID, userId);

        return isUserDelete > 0;
    }

    @Override
    public boolean isExistsById(Long userId) {

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_USER_BY_ID, Boolean.class, userId));
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {

        int addedFriend = jdbcTemplate.update(ADD_FRIEND, userId, friendId, userId, friendId, friendId, userId);

        return addedFriend > 0;
    }

    @Override
    public List<User> findAllFriends(Long userId) {

        List<User> users = jdbcTemplate.query(FIND_ALL_FRIENDS_BY_USER_ID, this::mapRowToUser, userId, userId);

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherId) {

        List<User> users = jdbcTemplate.query(FIND_COMMON_FRIENDS, this::mapRowToUser, userId, userId, otherId, otherId);

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {

        int isFriendDeleted = jdbcTemplate.update(DELETE_FRIEND, userId, friendId, friendId, userId);

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
