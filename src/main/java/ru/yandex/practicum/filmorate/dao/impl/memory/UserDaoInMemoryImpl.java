package ru.yandex.practicum.filmorate.dao.impl.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository(value = "userMemory")
public class UserDaoInMemoryImpl implements UserDao {
    private static final Map<Long, User> USERS = new HashMap<>();
    private Long id = 0L;

    @Override
    public User save(User newObject) {

        newObject.setId(generateId());
        USERS.put(newObject.getId(), newObject);

        return newObject;
    }

    @Override
    public Optional<User> findById(Long userId) {

        return Optional.ofNullable(USERS.get(userId));
    }

    @Override
    public List<User> findAll() {

        return new ArrayList<>(USERS.values());
    }

    @Override
    public Optional<User> update(User updateObject) {

        Long id = updateObject.getId();

        if (USERS.containsKey(id)) {
            USERS.put(id, updateObject);
            return Optional.of(updateObject);
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long userId) {

        return USERS.remove(userId) != null;
    }

    @Override
    public boolean isExistsById(Long userId) {

        return USERS.containsKey(userId);
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {

        USERS.get(userId).getFriends().add(friendId);
        return USERS.get(friendId).getFriends().add(userId);
    }

    @Override
    public List<User> findAllFriends(Long userId) {

        return USERS.get(userId).getFriends().stream()
                .map(USERS::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherId) {

        Set<Long> userFriends = USERS.get(userId).getFriends();

        return USERS.get(otherId).getFriends().stream()
                .filter(userFriends::contains)
                .map(USERS::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {

        USERS.get(userId).getFriends().remove(friendId);
        return USERS.get(friendId).getFriends().remove(userId);
    }

    private Long generateId() {

        return ++id;
    }
}
