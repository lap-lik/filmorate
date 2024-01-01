package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.constant.UserConstant.EMAIL_REGEX;
import static ru.yandex.practicum.filmorate.constant.UserConstant.LOGIN_REGEX;

@Data
@Builder
public class User {
    private Long id;

    @NotBlank(message = "The email cannot be empty.")
    @Email(regexp = EMAIL_REGEX, message = "The email is incorrect.")
    private String email;

    @NotBlank(message = "The login cannot be empty.")
    @Pattern(regexp = LOGIN_REGEX, message = "The login must not contain spaces.")
    private String login;

    private String name;

    @Past(message = "The date of birth cannot be in the future.")
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();
}