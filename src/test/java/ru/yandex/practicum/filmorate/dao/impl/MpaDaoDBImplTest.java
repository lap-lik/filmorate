package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.SQLDataAccessException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Sql(scripts = "classpath:test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MpaDaoDBImplTest extends AbstractDaoTest {
    private final JdbcTemplate jdbcTemplate;
    private MpaDao mpaDao;

    @BeforeEach
    void setUp() {
        mpaDao = new MpaDaoDBImpl(jdbcTemplate);
    }

    @Test
    void save() {

        // Подготавливаем данные для теста
        setUpMpa();

        // вызываем тестируемый метод
        Mpa result = mpaDao.save(mpa1);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей сохраненного рейтинга MPA
                .isEqualTo(mpa1); // проверяем что сохраненный объект и передаваемый идентичны
    }

    @Test
    void save_Empty_Name() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpa1.setName(""); //устанавливаем пустое имя

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> mpaDao.save(mpa1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the mpa in the DB."); // проверяем текст ошибки
    }

    @Test
    void update() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpaDao.save(mpa1);

        // вызываем тестируемый метод
        Mpa result = mpaDao.update(mpaUpdate).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей обновленного рейтинга MPA
                .isEqualTo(mpaUpdate);
    }

    @Test
    void update_Invalid_Id() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpaDao.save(mpa1);
        mpaUpdate.setId(999L);

        // вызываем тестируемый метод
        Mpa result = mpaDao.update(mpaUpdate).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет рейтинга MPA с ID = 999
    }

    @Test
    void findById() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpaDao.save(mpa1);
        mpaDao.save(mpa2);

        // вызываем тестируемый метод
        Mpa result = mpaDao.findById(2L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей запрошенного рейтинга MPA по id=2
                .isEqualTo(mpa2);
    }

    @Test
    void findById_NotFound_Return_Null() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpaDao.save(mpa1);
        mpaDao.save(mpa2);

        // вызываем тестируемый метод
        Mpa result = mpaDao.findById(999L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет рейтинга MPA c ID = 999
    }

    @Test
    void findAll() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpaDao.save(mpa1);
        mpaDao.save(mpa2);

        // вызываем тестируемый метод
        List<Mpa> result = mpaDao.findAll();

        // проверяем утверждения
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(2); // проверяем что метод возвращает верное количество рейтингов MPA
    }

    @Test
    void deleteById() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpaDao.save(mpa1);
        mpaDao.save(mpa2);

        // вызываем тестируемый метод
        mpaDao.deleteById(1L);

        // проверяем утверждения
        List<Mpa> result = mpaDao.findAll();
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(1); // проверяем что удалился рейтинг MPA с id=1
    }

    @Test
    void isExistsById() {

        // Подготавливаем данные для теста
        setUpMpa();
        mpaDao.save(mpa1);
        mpaDao.save(mpa2);

        // вызываем тестируемый метод
        boolean result = mpaDao.isExistsById(2L);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(true); // проверяем что метод подтверждает наличие рейтинга MPA с id=2 в DB
    }
}