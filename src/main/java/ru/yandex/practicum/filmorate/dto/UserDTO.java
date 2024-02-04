package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

import static ru.yandex.practicum.filmorate.constant.UserConstant.EMAIL_REGEX;
import static ru.yandex.practicum.filmorate.constant.UserConstant.LOGIN_REGEX;

@Data
@Builder
public class UserDTO {

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class, message = "The ID must not be null.")
    private Long id;

    @NotBlank(message = "The email must not be empty.")
    @Email(regexp = EMAIL_REGEX, message = "The email is incorrect.")
    private String email;

    @NotBlank(message = "The login must not be empty.")
    @Pattern(regexp = LOGIN_REGEX, message = "The login must not contain spaces.")
    private String login;

    private String name;

    @NotNull(message = "Birthday must not be null.")
    @PastOrPresent(message = "The date of birth cannot be in the future.")
    private LocalDate birthday;

    private Set<Long> friends;
}
