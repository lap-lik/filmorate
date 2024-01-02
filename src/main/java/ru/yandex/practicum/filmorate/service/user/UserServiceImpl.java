package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.RequestValidator;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final RequestValidator requestValidator;

    @Override
    public User createUser(User newUser) {
        log.info("Request to create a user: ID - `{}` Name - `{}`.", newUser.getId(), newUser.getName());
        requestValidator.validationRequest(newUser);
        validateUserName(newUser);
        User user = userDao.saveUser(newUser);
        log.info("The user has been created: ID - `{}` Name - `{}`.", user.getId(), user.getName());
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Request to receive a user by ID - `{}`", userId);
        User user = userDao.findUserById(userId)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The user with the ID - `%d` was not found.", userId))
                        .httpStatus(NOT_FOUND).build());
        log.info("A user has been received: ID - `{}` Name - `{}`.", user.getId(), user.getName());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Request for a list of users.");
        List<User> users = userDao.findAllUsers();
        if (users.isEmpty()) {
            log.warn("The list of users is empty.");
        } else {
            log.info("The list of users has been received.");
        }
        return users;
    }

    @Override
    public User updateUser(User updateUser) {
        log.info("User update request: ID - `{}` Name - `{}`..", updateUser.getId(), updateUser.getName());
        requestValidator.validationRequest(updateUser);
        validateUserName(updateUser);
        User user = userDao.updateUser(updateUser)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The user %s was not found.", updateUser.getLogin()))
                        .httpStatus(NOT_FOUND).build());
        log.info("User has been updated: ID - `{}` Name - `{}`.", user.getId(), user.getName());
        return user;
    }

    @Override
    public User deleteUserById(Long userId) {
        log.info("Request was received to delete a user with the ID - `{}`.", userId);
        User user = userDao.deleteUserById(userId)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The user with the ID - `%d` was not found.", userId))
                        .httpStatus(NOT_FOUND).build());
        log.info("The user has been deleted: ID - `{}` Name - `{}`.", user.getId(), user.getName());
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Request was received to add a user with the ID - `{}` to the friends of a user with the ID - `{}`",
                friendId, userId);
        checkAddedFriend(userId, friendId);
        User friend = userDao.addFriend(userId, friendId);
        log.info("Friend has been added: ID - `{}` Name - `{}`.", friend.getId(), friend.getName());
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        log.info("Request was received for a list of the user friends with the ID - `{}`.", userId);
        if (!userDao.existsUserById(userId)) {
            throw NotFoundException.builder()
                    .message(String.format("The user with the ID - `%d` was not found.", userId))
                    .httpStatus(NOT_FOUND).build();
        }
        List<User> friends = userDao.findAllFriends(userId);
        if (friends.isEmpty()) {
            log.warn("The list of friends is empty.");
        } else {
            log.info("The list of friends of the user with the ID - `{}` has been received.", userId);
        }
        return friends;
    }

    @Override
    public void deleteFriendById(Long userId, Long friendId) {
        log.info("A request was received to remove a user with the ID - `{}` " +
                "from the list of friends of a user with the ID - `{}`.", friendId, userId);
        checkDeletedFriend(userId, friendId);
        User friend = userDao.deleteFriendById(userId, friendId);
        log.info("The user has been deleted from friends: ID - `{}` Name - `{}`.", friend.getId(), friend.getName());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Request was received for a list of common friends of users with IDs `{}` and `{}`.", userId, otherId);
        checkId(userId, otherId);
        List<User> friends = userDao.findCommonFriends(userId, otherId);
        if (friends.isEmpty()) {
            log.warn("Users do not have common friends. The list of common friends is empty.");
        } else {
            log.info("A list of common friends of users with IDs `{}` and `{}` has been received.", userId, otherId);
        }
        return friends;
    }

    private void validateUserName(User user) {
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            log.warn("The user name cannot be empty, the username value is used for the name.");
            user.setName(user.getLogin());
        }
    }

    private void checkAddedFriend(Long userId, Long friendId) {
        checkId(userId, friendId);
        if (userDao.isFriend(userId, friendId)) {
            log.error(String.format("Friend by ID - `%d` has already been added.", friendId));
            throw BadRequestException.builder()
                    .message(String.format("Friend by ID - `%d` has already been added.", friendId))
                    .httpStatus(BAD_REQUEST)
                    .build();
        }
    }

    private void checkDeletedFriend(Long userId, Long friendId) {
        checkId(userId, friendId);
        if (!userDao.isFriend(userId, friendId)) {
            log.error(String.format("The user by ID - `%d` is not in the friends list.", friendId));
            throw BadRequestException.builder()
                    .message(String.format("The user by ID - `%d` is not in the friends list.", friendId))
                    .httpStatus(BAD_REQUEST)
                    .build();
        }
    }

    private void checkId(Long userId, Long otherId) {
        StringBuilder messages = new StringBuilder();
        boolean userExists = userDao.existsUserById(userId);
        boolean friendExists = userDao.existsUserById(otherId);
        if (!userExists) {
            messages.append(String.format("User by ID - `%d` has not been found.", userId));
        }
        if (!friendExists) {
            if (messages.length() > 0) {
                messages.append(" ");
            }
            messages.append(String.format("User by ID - `%d` has not been found.", otherId));
        }
        if (!userExists || !friendExists) {
            log.error("Error {}", messages);
            throw NotFoundException.builder()
                    .message(messages.toString())
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }
}