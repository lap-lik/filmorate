package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.Mpa;

/**
 * The MpaMapper interface represents a mapper for converting Mpa entities to MpaDTOs and vice versa.
 *
 * @see GenericMapper
 */
@Mapper(componentModel = "spring")
public interface MpaMapper extends GenericMapper<Mpa, MpaDTO> {
}