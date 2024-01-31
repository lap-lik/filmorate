package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Optional;

/**
 * The GenericDao interface represents a generic data access object (DAO) for managing entities.
 *
 * @param <T>  The entity type.
 * @param <ID> The identifier type.
 */
public interface GenericDao<T, ID> {

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id The identifier of the entity.
     * @return An Optional containing the entity if found, or empty if not found.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities of type T.
     *
     * @return A list of all entities.
     */
    List<T> findAll();

    /**
     * Saves an entity.
     *
     * @param entity The entity to be saved.
     * @return The saved entity.
     */
    T save(T entity);

    /**
     * Updates an existing entity.
     *
     * @param entity The entity to be updated.
     * @return An Optional containing the updated entity if found, or empty if not found.
     */
    Optional<T> update(T entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id The identifier of the entity to be deleted.
     * @return true if the entity was successfully deleted, or false if the entity was not found.
     */
    boolean deleteById(ID id);

    /**
     * Checks if an entity exists by its identifier.
     *
     * @param id The identifier of the entity to be checked.
     * @return true if the entity exists, or false if the entity does not exist.
     */
    boolean isExistsById(ID id);
}
