package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.MpaDTO;

/**
 * The MpaService interface represents a service for managing MPA ratings.
 * It extends the GenericService interface with MpaDTO as the entity type and Long as the identifier type.
 *
 * @see GenericService
 */
public interface MpaService extends GenericService<MpaDTO, Long> {

}