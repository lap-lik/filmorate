package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository(value = "userDB")
public class UserDaoDBImpl implements UserDao {
    @Override
    public Optional<User> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User save(User entity) {
        return null;
    }

    @Override
    public Optional<User> update(User entity) {
        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long aLong) {
        return false;
    }

    @Override
    public boolean isExistsById(Long aLong) {
        return false;
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        return null;
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherId) {
        return null;
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        return false;
    }
}
