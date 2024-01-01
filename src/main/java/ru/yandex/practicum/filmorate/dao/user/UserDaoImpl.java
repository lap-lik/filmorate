package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {
    private static final Map<Long, User> USERS = new HashMap<>();
    private Long id = 0L;

    @Override
    public User saveUser(User newObject) {
        newObject.setId(generateId());
        USERS.put(newObject.getId(), newObject);
        return newObject;
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(USERS.get(userId));
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(USERS.values());
    }

    @Override
    public Optional<User> updateUser(User updateObject) {
        Long id = updateObject.getId();
        if (USERS.containsKey(id)) {
            USERS.put(id, updateObject);
            return Optional.of(updateObject);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUserById(Long userId) {
        return Optional.ofNullable(USERS.remove(userId));
    }

    @Override
    public boolean existsUserById(Long userId) {
        return USERS.containsKey(userId);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        return USERS.get(userId).getFriends().contains(friendId);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        USERS.get(userId).getFriends().add(friendId);
        USERS.get(friendId).getFriends().add(userId);
        return USERS.get(friendId);
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        return USERS.get(userId).getFriends().stream()
                .map(USERS::get)
                .collect(Collectors.toList());
    }

    @Override
    public User deleteFriendById(Long userId, Long friendId) {
        USERS.get(userId).getFriends().remove(friendId);
        return USERS.get(friendId);
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = USERS.get(userId).getFriends();
        return USERS.get(otherId).getFriends().stream()
                .filter(userFriends::contains)
                .map(USERS::get)
                .collect(Collectors.toList());
    }

    private Long generateId() {
        return ++id;
    }
}
