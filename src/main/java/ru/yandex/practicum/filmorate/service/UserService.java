package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.UserDTO;

import java.util.List;

/**
 * The UserService interface represents a service for managing users.
 * It extends the GenericService interface with UserDTO as the entity type and Long as the identifier type.
 * The methods provided in this interface allow adding, retrieving and deleting friends for a user.
 *
 * @see GenericService
 */
public interface UserService extends GenericService<UserDTO, Long> {

    /**
     * Adds a friend to the user with the specified ID.
     *
     * @param id       The ID of the user.
     * @param friendId The ID of the friend to be added.
     */
    void addFriend(Long id, Long friendId);

    /**
     * Retrieves a list of all friends for the user with the specified ID.
     *
     * @param id The ID of the user.
     * @return A list of UserDTO objects representing the user's friends.
     */
    List<UserDTO> getAllFriends(Long id);

    /**
     * Deletes a friend from the user with the specified ID.
     *
     * @param id      The ID of the user.
     * @param otherId The ID of the other user.
     * @return A list of UserDTO objects representing the common friends between the two users.
     */
    List<UserDTO> getCommonFriends(Long id, Long otherId);

    /**
     * Deletes a friend from the user with the specified ID.
     *
     * @param id       The ID of the user.
     * @param friendId The ID of the friend to be deleted.
     */
    void deleteFriendById(Long id, Long friendId);
}
