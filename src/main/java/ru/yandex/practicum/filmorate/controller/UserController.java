package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        log.info("Вызван список пользователей.");
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        Integer userId = user.getId();
        if (!users.containsKey(userId)) {
            log.error("Ошибка обновления: пользователь с ID {} не найден.", userId);
            return ResponseEntity.status(404).body(user);
        }
        users.put(userId, user);
        log.info("Обновлен пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    private Integer generateId() {
        return ++id;
    }
}
