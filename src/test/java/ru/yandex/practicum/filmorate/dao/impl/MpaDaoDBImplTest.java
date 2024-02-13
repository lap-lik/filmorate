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
class MpaDaoDBImplTest {
    private final JdbcTemplate jdbcTemplate;
    private MpaDao mpaDao;
    private Mpa mpa1;
    private Mpa mpa2;
    private Mpa mpaUpdate;

    @BeforeEach
    void setUp() {

        mpaDao = new MpaDaoDBImpl(jdbcTemplate);
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

    @Test
    void testSaveMpaWithExpectedResultNotNull() {

        // вызываем тестируемый метод
        Mpa result = mpaDao.save(mpa1);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей сохраненного рейтинга MPA
                .isEqualTo(mpa1); // проверяем что сохраненный объект и передаваемый идентичны
    }

    @Test
    void testSaveMpaWithEmptyNameResultException() {

        // Подготавливаем данные для теста
        mpa1.setName(""); //устанавливаем пустое имя

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> mpaDao.save(mpa1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the mpa in the DB."); // проверяем текст ошибки
    }

    @Test
    void testUpdateMpaWithExpectedResultNotNull() {

        // Подготавливаем данные для теста
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
    void testUpdateMpaWithInvalidMpaIdResultNull() {

        // Подготавливаем данные для теста
        mpaDao.save(mpa1);
        mpaUpdate.setId(999L);

        // вызываем тестируемый метод
        Mpa result = mpaDao.update(mpaUpdate).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет рейтинга MPA с ID = 999
    }

    @Test
    void testFindMpaByIdWithExpectedResultNotNull() {

        // Подготавливаем данные для теста
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
    void testFindMpaByInvalidIdResultNull() {

        // Подготавливаем данные для теста
        mpaDao.save(mpa1);
        mpaDao.save(mpa2);

        // вызываем тестируемый метод
        Mpa result = mpaDao.findById(999L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет рейтинга MPA c ID = 999
    }

    @Test
    void testFindAllResultListOfMpaRatings() {

        // Подготавливаем данные для теста
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
    void testDeleteByMpaIdResultMpaDeleted() {

        // Подготавливаем данные для теста
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
    void testMpaExistenceByIdResultTrue() {

        // Подготавливаем данные для теста
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