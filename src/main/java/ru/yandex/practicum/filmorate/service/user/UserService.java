package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User newUser);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(User updateUser);

    User deleteUserById(Long id);

    void addFriend(Long id, Long friendId);

    List<User> getAllFriends(Long id);

    void deleteFriendById(Long id, Long friendId);

    List<User> getCommonFriends(Long id, Long otherId);
}
