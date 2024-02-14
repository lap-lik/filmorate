package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

/**
 * The UserMapper interface represents a mapper for converting User entities to UserDTOs and vice versa.
 *
 * @see GenericMapper
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, UserDTO> {
}
