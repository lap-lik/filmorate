package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody final UserDTO userDTO) {

        log.info("START endpoint `method:POST /users` (create user), request: {}.", userDTO);
        UserDTO response = service.create(userDTO);
        log.info("END endpoint `method:POST /users` (create user), response: {}.", response);

        return response;
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable final Long id) {

        log.info("START endpoint `method:GET /users/{id}` (get user by id), user id: {}.", id);
        UserDTO response = service.getById(id);
        log.info("END endpoint `method:GET /users/{id}` (get user by id), response: {}.", response);

        return response;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {

        log.info("START endpoint `method:GET /users` (get all users).");
        List<UserDTO> response = service.getAll();
        log.info("END endpoint `method:GET /users` (get all users), response-size: {}.", response.size());

        return response;
    }

    @PutMapping
    public UserDTO updateUser(@RequestBody UserDTO userDTO) {

        log.info("START endpoint `method:PUT /users` (update user), request: {}.", userDTO);
        UserDTO response = service.update(userDTO);
        log.info("END endpoint `method:PUT /users` (update user), response: {}.", response);

        return response;
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {

        log.info("START endpoint `method:DELETE /users/{id}` (delete user by id), user id: {}.", id);
        service.deleteById(id);
        log.info("END endpoint `method:DELETE /users/{id}` (delete user by id), response: HttpStatus.NO_CONTENT.");
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("START endpoint `method:PUT /users/{id}/friends/{friendId}` (add friend), " +
                "user id: {}, friend id: {}.", id, friendId);
        service.addFriend(id, friendId);
        log.info("END endpoint `method:PUT /users/{id}/friends/{friendId}` (add friend), response: HttpStatus.OK.");
    }

    @GetMapping("/{id}/friends")
    public List<UserDTO> getAllFriends(@PathVariable Long id) {

        log.info("START endpoint `method:GET /users/{id}/friends` (get all friends), user id: {}.", id);
        List<UserDTO> response = service.getAllFriends(id);
        log.info("END endpoint `method:GET /users/{id}/friends` (get all friends), response-size: {}.", response.size());

        return response;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDTO> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {

        log.info("START endpoint `method:GET /users/{id}/friends/common/{otherId}` (get common friends), " +
                "user id: {}, other user id: {}.", id, otherId);
        List<UserDTO> response = service.getCommonFriends(id, otherId);
        log.info("END endpoint `method:GET /users/{id}/friends/common/{otherId}` (get common friends), " +
                "response-size: {}.", response.size());

        return response;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendById(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("START endpoint `method:DELETE /users/{id}/friends/{friendId}` (delete friend), " +
                "user id: {}, friend id: {}.", id, friendId);
        service.deleteFriendById(id, friendId);
        log.info("END endpoint `method:DELETE /users/{id}/friends/{friendId}` (delete friend), response: HttpStatus.NO_CONTENT.");
    }
}
