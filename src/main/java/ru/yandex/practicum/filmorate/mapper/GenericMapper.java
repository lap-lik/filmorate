package ru.yandex.practicum.filmorate.mapper;

import java.util.List;

/**
 * The GenericMapper interface represents a generic mapper that converts entities to Dos and vice versa.
 * Based on the MapStruct framework.
 *
 * @param <E> The entity type.
 * @param <D> The DTO type.
 */
public interface GenericMapper<E, D> {

    /**
     * Converts a DTO object to an entity.
     *
     * @param dto The DTO object to be converted.
     * @return The corresponding entity.
     */
    E toEntity(D dto);

    /**
     * Converts an entity object to a DTO.
     *
     * @param entity The entity object to be converted.
     * @return The corresponding DTO.
     */
    D toDTO(E entity);

    /**
     * Converts a list of DTOs to a list of entities.
     *
     * @param dtos The list of DTOs to be converted.
     * @return The corresponding list of entities.
     */
    List<E> toEntities(List<D> dtos);

    /**
     * Converts a list of entities to a list of DTOs.
     *
     * @param entities The list of entities to be converted.
     * @return The corresponding list of DTOs.
     */
    List<D> toDTOs(List<E> entities);
}
