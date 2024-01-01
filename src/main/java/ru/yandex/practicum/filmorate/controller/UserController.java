package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getUserById(id));
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllUsers());
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        return service.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.deleteUserById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendById(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriendById(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getAllFriends(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getCommonFriends(id, otherId));
    }
}
