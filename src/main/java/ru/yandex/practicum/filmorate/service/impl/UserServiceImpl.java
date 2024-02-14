package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.utils.ValidatorUtils;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.yandex.practicum.filmorate.constant.UserConstant.USER_DAO_IMPL;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(@Qualifier(value = USER_DAO_IMPL) UserDao userDao,
                           UserMapper mapper) {
        this.userDao = userDao;
        this.mapper = mapper;
    }

    @Override
    public UserDTO create(UserDTO userDTO) {

        ValidatorUtils.validate(userDTO, Marker.OnCreate.class);
        ValidatorUtils.validateUserName(userDTO);

        return mapper.toDTO(userDao.save(mapper.toEntity(userDTO)));
    }

    @Override
    public UserDTO getById(Long userId) {

        return mapper.toDTO(userDao.findById(userId)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The user with the ID - `%d` was not found.", userId))
                        .httpStatus(NOT_FOUND)
                        .build()));
    }

    @Override
    public List<UserDTO> getAll() {

        return mapper.toDTOs(userDao.findAll());
    }

    @Override
    public UserDTO update(UserDTO userDTO) {

        ValidatorUtils.validate(userDTO, Marker.OnUpdate.class);
        ValidatorUtils.validateUserName(userDTO);

        return mapper.toDTO(userDao.update(mapper.toEntity(userDTO))
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("The user `%s` was not found.", userDTO.getLogin()))
                        .httpStatus(NOT_FOUND)
                        .build()));
    }

    @Override
    public void deleteById(Long userId) {

        boolean isUserDeleted = userDao.deleteById(userId);

        if (!isUserDeleted) {
            throw NotFoundException.builder()
                    .message(String.format("The user with the ID - `%d` was not found.", userId))
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        checkIds(userId, friendId);
        boolean isFriendAdded = userDao.addFriend(userId, friendId);

        if (!isFriendAdded) {
            throw BadRequestException.builder()
                    .message(String.format("The friend by ID - `%d` has already been added.", friendId))
                    .httpStatus(BAD_REQUEST)
                    .build();
        }
    }

    @Override
    public List<UserDTO> getAllFriends(Long userId) {

        if (!userDao.isExistsById(userId)) {
            throw NotFoundException.builder()
                    .message(String.format("The user with the ID - `%d` was not found.", userId))
                    .httpStatus(NOT_FOUND)
                    .build();
        }

        return mapper.toDTOs(userDao.findAllFriends(userId));
    }

    @Override
    public List<UserDTO> getCommonFriends(Long userId, Long otherId) {

        checkIds(userId, otherId);

        return mapper.toDTOs(userDao.findCommonFriends(userId, otherId));
    }

    @Override
    public void deleteFriendById(Long userId, Long friendId) {

        checkIds(userId, friendId);

        boolean isFriendDeleted = userDao.deleteFriend(userId, friendId);

        if (!isFriendDeleted) {
            throw BadRequestException.builder()
                    .message(String.format("The user by ID - `%d` was not found in the friends list.", friendId))
                    .httpStatus(BAD_REQUEST)
                    .build();
        }
    }

    private void checkIds(Long firstUserId, Long secondUserId) {

        List<String> messages = new ArrayList<>();
        boolean firstUserExists = userDao.isExistsById(firstUserId);
        boolean secondUserExists = userDao.isExistsById(secondUserId);

        if (!firstUserExists) {
            messages.add(String.format("The user by ID - `%d` was not found.", firstUserId));
        }

        if (!secondUserExists) {
            messages.add(String.format("The user by ID - `%d` was not found.", secondUserId));
        }

        if (!messages.isEmpty()) {
            throw NotFoundException.builder()
                    .message(String.join(" & ", messages))
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }
}