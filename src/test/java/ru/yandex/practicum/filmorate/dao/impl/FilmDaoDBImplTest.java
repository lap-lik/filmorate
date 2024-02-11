package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.SQLDataAccessException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDaoDBImplTest extends AbstractDaoTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDao filmDao;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        filmDao = new FilmDaoDBImpl(jdbcTemplate);
        userDao = new UserDaoDBImpl(jdbcTemplate);
    }

    @Test
    void save() {

        // Подготавливаем данные для теста
        setUpFilms();
        setUpGenres();
        setUpGenresOnlyIds();
        listGenreIds.add(genreOnlyId2);
        film1.setGenres(listGenreIds);

        // вызываем тестируемый метод
        Film result = filmDao.save(film1);

        // проверяем утверждения
        listGenres.add(genre2);
        film1.setGenres(listGenres);

        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей сохраненного фильма
                .ignoringFields("likedUserIds") // пропускаем проверку полей
                .isEqualTo(film1); // проверяем что сохраненный объект и передаваемый идентичны
    }

    @Test
    void save_Empty_Name() {

        // Подготавливаем данные для теста
        setUpFilms();
        film1.setName(""); //устанавливаем пустое имя

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> filmDao.save(film1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the film in the DB."); // проверяем текст ошибки
    }

    @Test
    void save_More_Max_Size_Description() {

        // Подготавливаем данные для теста
        setUpFilms();
        film1.setDescription("A".repeat(201)); //устанавливаем значение больше заданного

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> filmDao.save(film1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the film in the DB."); // проверяем текст ошибки
    }

    @Test
    void save_Invalid_ReleaseDate() {

        // Подготавливаем данные для теста
        setUpFilms();
        film1.setReleaseDate(LocalDate.of(1800, 1, 1)); //устанавливаем неправильную дату

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> filmDao.save(film1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the film in the DB."); // проверяем текст ошибки
    }

    @Test
    void save_Negative_Duration() {

        // Подготавливаем данные для теста
        setUpFilms();
        film1.setDuration(-100);

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> filmDao.save(film1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the film in the DB."); // проверяем текст ошибки
    }

    @Test
    void findById() {

        // Подготавливаем данные для теста
        setUpFilms();
        filmDao.save(film1);
        filmDao.save(film2);

        // вызываем тестируемый метод
        Film result = filmDao.findById(2L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей запрошенного фильма по id=2
                .ignoringFields("likedUserIds", "genres") // пропускаем проверку полей
                .isEqualTo(film2);
    }

    @Test
    void findById_NotFound_Return_Null() {

        // Подготавливаем данные для теста
        setUpFilms();
        filmDao.save(film1);
        filmDao.save(film2);

        // вызываем тестируемый метод
        Film result = filmDao.findById(999L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет фаильма c ID = 999
    }

    @Test
    void findAll() {

        // Подготавливаем данные для теста
        setUpFilms();
        filmDao.save(film1);
        filmDao.save(film2);

        // вызываем тестируемый метод
        List<Film> result = filmDao.findAll();

        // проверяем утверждения
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(2); // проверяем что findAll возвращает верное количество фильмов
    }

    @Test
    void update() {

        // Подготавливаем данные для теста
        setUpFilms();
        setUpGenres();
        setUpGenresOnlyIds();
        listGenreIds.add(genreOnlyId1);
        listGenreIds.add(genreOnlyId2);
        filmDao.save(film2);
        filmUpdate.setGenres(listGenreIds);

        // вызываем тестируемый метод
        Film result = filmDao.update(filmUpdate).orElse(null);

        // проверяем утверждения
        listGenres.add(genre1);
        listGenres.add(genre2);
        filmUpdate.setGenres(listGenres);
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей обновленного фильма
                .ignoringFields("likedUserIds") // пропускаем проверку полей
                .isEqualTo(filmUpdate);
    }

    @Test
    void update_Invalid_Id() {

        // вызываем тестируемый метод
        setUpFilms();
        filmDao.save(film1);
        filmUpdate.setId(999L);

        // вызываем тестируемый метод
        Film result = filmDao.update(filmUpdate).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет фильма с ID = 999
    }

    @Test
    void deleteById() {

        // Подготавливаем данные для теста
        setUpFilms();
        filmDao.save(film1);
        filmDao.save(film2);

        // вызываем тестируемый метод
        filmDao.deleteById(1L);

        // проверяем утверждения
        List<Film> result = filmDao.findAll();
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(1); // проверяем что удалился фильм с id=1
    }

    @Test
    void isExistsById() {

        // Подготавливаем данные для теста
        setUpFilms();
        filmDao.save(film1);
        filmDao.save(film2);

        // вызываем тестируемый метод
        boolean result = filmDao.isExistsById(2L);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(true); // проверяем что метод подтверждает наличие фильма с id=2 в DB
    }

    @Test
    void addLike() {

        // Подготавливаем данные для теста
        setUpFilms();
        setUpUsers();
        filmDao.save(film1);
        userDao.save(user1);
        Long film1Id = film1.getId();
        Long user1Id = user1.getId();

        // вызываем тестируемый метод
        boolean result = filmDao.addLike(film1Id, user1Id);

        // проверяем утверждения
        assertThat(result)
                .isEqualTo(true); // проверяем что метод подтверждает добавление лайка DB

        // повторно вызываем тестируемый метод
        boolean result2 = filmDao.addLike(film1Id, user1Id); // повторяем добавление уже существующего лайка

        // проверяем утверждения
        assertThat(result2)
                .isEqualTo(false); // проверяем что метод не подтверждает добавление уже существующего лайка DB
    }

    @Test
    void findPopularFilms() {

        // Подготавливаем данные для теста
        setUpFilms();
        setUpUsers();
        filmDao.save(film1);
        filmDao.save(film2);
        userDao.save(user1);
        userDao.save(user2);
        Long film2Id = film2.getId();
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();
        filmDao.addLike(film2Id, user1Id);
        filmDao.addLike(film2Id, user2Id);
        System.out.println(filmDao.findById(2L));

        // вызываем тестируемый метод
        List<Film> result = filmDao.findPopularFilms(2);

        // проверяем утверждения
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(2); // проверяем что findPopularFilms возвращает верное количество фильмов
        assertThat(result.get(0))
                .usingRecursiveComparison() // проверяем значения полей обновленного фильма
                .ignoringFields("likedUserIds", "genres") // пропускаем проверку полей
                .isEqualTo(film2); // проверяем что findPopularFilms возвращает фильмы в порядке убывания популярности
    }

    @Test
    void deleteLike() {

        // Подготавливаем данные для теста
        setUpFilms();
        setUpUsers();
        filmDao.save(film1);
        userDao.save(user1);
        userDao.save(user2);
        Long film1Id = film1.getId();
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();
        filmDao.addLike(film1Id, user1Id);
        filmDao.addLike(film1Id, user2Id);

        // вызываем тестируемый метод
        boolean result = filmDao.deleteLike(film1Id, user1Id);

        // проверяем утверждения
        assertThat(result)
                .isEqualTo(true); // проверяем что метод подтверждает удаление лайка из DB

        // повторно вызываем тестируемый метод
        boolean result2 = filmDao.deleteLike(film1Id, user1Id); // повторяем удаление уже удаленного лайка

        // проверяем утверждения
        assertThat(result2)
                .isEqualTo(false); // проверяем что метод не подтверждает удаление несуществующего лайка из DB
    }
}