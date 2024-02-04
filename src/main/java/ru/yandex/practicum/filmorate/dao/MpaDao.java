package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

/**
 * The MpaDAO interface represents a data access object for managing the MPA rating.
 * It extends the GenericDAO interface by using Mpa as the entity type and Long as the identifier type.
 * This interface uses standard CRUD methods inherited from the parent class.
 *
 * @see GenericDao
 */
public interface MpaDao extends GenericDao<Mpa, Long>{
}
