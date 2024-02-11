package ru.yandex.practicum.filmorate.dao.impl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractDaoTest {
    protected Film film1;
    protected Film film2;
    protected Film filmUpdate;
    protected Set<Genre> listGenreIds;
    protected Set<Genre> listGenres;
    protected Genre genre1;
    protected Genre genreOnlyId1;
    protected Genre genre2;
    protected Genre genreOnlyId2;
    protected Genre genreUpdate;
    protected Mpa mpa1;
    protected Mpa mpa2;
    protected Mpa mpaUpdate;
    protected User user1;
    protected User user2;
    protected User user3;
    protected User user4;
    protected User userUpdate;

    void setUpGenres() {

        genre1 = Genre.builder()
                .id(1L)
                .name("Комедия")
                .build();

        genre2 = Genre.builder()
                .id(2L)
                .name("Драма")
                .build();

        genreUpdate = Genre.builder()
                .id(1L)
                .name("Боевик")
                .build();

        listGenres = new TreeSet<>(Comparator.comparing(Genre::getId));
    }

    void setUpMpa() {

        mpa1 = Mpa.builder()
                .id(1L)
                .name("G")
                .build();

        mpa2 = Mpa.builder()
                .id(2L)
                .name("PG")
                .build();

        mpaUpdate = Mpa.builder()
                .id(1L)
                .name("PG-13")
                .build();
    }

    void setUpGenresOnlyIds() {

        genreOnlyId1 = Genre.builder().id(1L).build();

        genreOnlyId2 = Genre.builder().id(2L).build();

        listGenreIds = new TreeSet<>(Comparator.comparing(Genre::getId));
    }

    void setUpFilms() {

        film1 = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .mpa(Mpa.builder().id(1L).name("G").build())
                .build();

        film2 = Film.builder()
                .name("New film")
                .description("New film about friends")
                .releaseDate(LocalDate.of(1999, 4, 30))
                .duration(120)
                .mpa(Mpa.builder().id(3L).name("PG-13").build())
                .build();

        filmUpdate = Film.builder()
                .id(1L)
                .name("Film Updated")
                .description("New film update description")
                .releaseDate(LocalDate.of(1989, 4, 17))
                .duration(190)
                .mpa(Mpa.builder().id(2L).name("PG").build())
                .build();
    }


    void setUpUsers() {

        user1 = User.builder()
                .login("user-2")
                .name("User-1 Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1984, 1, 4))
                .build();

        user2 = User.builder()
                .login("user-2")
                .name("User-2 Name")
                .email("user-2@mail.ru")
                .birthday(LocalDate.of(1983, 2, 3))
                .build();

        user3 = User.builder()
                .login("user-3")
                .name("User-3 Name")
                .email("user-3@mail.ru")
                .birthday(LocalDate.of(1982, 3, 2))
                .build();

        user4 = User.builder()
                .login("user-4")
                .name("User-4 Name")
                .email("user-4@mail.ru")
                .birthday(LocalDate.of(1981, 4, 1))
                .build();

        userUpdate = User.builder()
                .id(1L)
                .login("User-1-Update")
                .name("User-1 Name-Update")
                .email("user-1@mail.ru")
                .friends(Set.of(2L))
                .birthday(LocalDate.of(1985, 5, 5))
                .build();
    }
}
