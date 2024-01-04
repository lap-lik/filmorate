package ru.yandex.practicum.filmorate.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User saveUser(User newObject);

    Optional<User> findUserById(final Long userId);

    List<User> findAllUsers();

    Optional<User> updateUser(User updateObject);

    Optional<User> deleteUserById(final Long userId);

    boolean existsUserById(Long userId);

    boolean isFriend(Long userId, Long friendId);

    User addFriend(final Long userId, final Long friendId);

    List<User> findAllFriends(final Long userId);

    User deleteFriendById(final Long userId, final Long friendId);

    List<User> findCommonFriends(final Long userId, final Long otherId);
}
