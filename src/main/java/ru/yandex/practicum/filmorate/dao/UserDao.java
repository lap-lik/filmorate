package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * The UserDao interface represents a data access object for managing users.
 * It extends the GenericDao interface with User as the entity type and Long as the identifier type.
 * The methods provided in this interface allow adding, retrieving, and deleting friends for a user.
 *
 * @see GenericDao
 */
public interface UserDao extends GenericDao<User, Long> {

    /**
     * Adds a friend to the user with the specified user ID.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend to be added.
     * @return true if the friend is added successfully, false otherwise.
     */
    boolean addFriend(final Long userId, final Long friendId);

    /**
     * Retrieves a list of all friends for a user with the specified user ID.
     *
     * @param userId The ID of the user.
     * @return A list of User objects representing the user's friends.
     */
    List<User> findAllFriends(final Long userId);

    /**
     * Retrieves a list of common friends between two users.
     *
     * @param userId  The ID of the first user.
     * @param otherId The ID of the second user.
     * @return A list of User objects representing the common friends.
     */
    List<User> findCommonFriends(final Long userId, final Long otherId);

    /**
     * Deletes a friend from the user with the specified user ID.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend to be deleted.
     * @return true if the friend is deleted successfully, false otherwise.
     */
    boolean deleteFriend(final Long userId, final Long friendId);
}
