package ru.yandex.practicum.filmorate.service;

import java.util.List;

/**
 * The GenericService interface represents a generic service for managing entities.
 *
 * @param <T>  The entity type.
 * @param <ID> The identifier type.
 */
public interface GenericService<T, ID> {

    /**
     * Creates a new entity.
     *
     * @param dto The DTO object representing the entity to be created.
     * @return The created entity.
     */
    T create(T dto);

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id The identifier of the entity.
     * @return The entity with the specified identifier, or null if not found.
     */
    T getById(ID id);

    /**
     * Retrieves a list of all entities.
     *
     * @return A list containing all entities.
     */
    List<T> getAll();

    /**
     * Updates an entity.
     *
     * @param dto The DTO object representing the entity to be updated.
     * @return The updated entity.
     */
    T update(T dto);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id The identifier of the entity to be deleted.
     */
    void deleteById(ID id);
}
