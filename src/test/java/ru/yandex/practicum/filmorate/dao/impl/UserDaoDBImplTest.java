package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.SQLDataAccessException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDaoDBImplTest extends AbstractDaoTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoDBImpl(jdbcTemplate);
    }


    @Test
    void save() {

        // Подготавливаем данные для теста
        setUpUsers();

        // вызываем тестируемый метод
        User result = userDao.save(user1);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей сохраненного пользователя
                .ignoringFields("friends") // пропускаем проверку полей
                .isEqualTo(user1); // проверяем что сохраненный объект и передаваемый идентичны
    }

    @Test
    void save_Invalid_Email() {

        // Подготавливаем данные для теста
        setUpUsers();
        user1.setEmail("mail mail.ru"); //устанавливаем не правильный email

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> userDao.save(user1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the user in the DB."); // проверяем текст ошибки
    }

    @Test
    void save_Login_With_Space() {

        // Подготавливаем данные для теста
        setUpUsers();
        user1.setLogin("dol ore"); //устанавливаем логин с пробелом

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> userDao.save(user1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the user in the DB."); // проверяем текст ошибки
    }

    @Test
    void save_Invalid_Birthday() {

        // Подготавливаем данные для теста
        setUpUsers();
        user1.setBirthday(LocalDate.of(2025, 1, 1)); //устанавливаем дату рождения в будущем

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> userDao.save(user1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the user in the DB."); // проверяем текст ошибки
    }

    @Test
    void findById() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);

        // вызываем тестируемый метод
        User result = userDao.findById(2L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей запрошенного пользователя по id=2
                .ignoringFields("friends") // пропускаем проверку полей
                .isEqualTo(user2);
    }

    @Test
    void findById_NotFound_Return_Null() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);

        // вызываем тестируемый метод
        User result = userDao.findById(999L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет пользователя по ID = 999
    }

    @Test
    void findAll() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);

        // вызываем тестируемый метод
        List<User> result = userDao.findAll();

        // проверяем утверждения
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(2); // проверяем что findAll возвращает верное количество пользователей
    }

    @Test
    void update() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);
        userDao.addFriend(user1.getId(), user2.getId());

        // вызываем тестируемый метод
        User result = userDao.update(userUpdate).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем значения полей обновленного пользователя
                .isEqualTo(userUpdate);
    }

    @Test
    void update_Invalid_Id() {

        // вызываем тестируемый метод
        setUpUsers();
        userDao.save(user1);
        userUpdate.setId(999L);

        // вызываем тестируемый метод
        User result = userDao.update(userUpdate).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет пользователя с ID = 999
    }

    @Test
    void deleteById() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);

        // вызываем тестируемый метод
        userDao.deleteById(1L);

        // проверяем утверждения
        List<User> result = userDao.findAll();
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(1); // проверяем что удалился пользователь с id=2
    }

    @Test
    void isExistsById() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);

        // вызываем тестируемый метод
        boolean result = userDao.isExistsById(2L);

        // проверяем утверждения
        assertThat(result)
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(true); // проверяем что метод подтверждает наличие пользователя с id=2 в DB
    }

    @Test
    void addFriend() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();

        // вызываем тестируемый метод
        boolean result = userDao.addFriend(user1Id, user2Id);

        // проверяем утверждения
        assertThat(result)
                .isEqualTo(true); // проверяем что метод подтверждает добавление друга DB

        // повторно вызываем тестируемый метод
        boolean result2 = userDao.addFriend(user1Id, user2Id); // повторяем добавление уже существующего друга

        // проверяем утверждения
        assertThat(result2)
                .isEqualTo(false); // проверяем что метод не подтверждает добавление уже существующего друга DB

        // повторно вызываем тестируемый метод c другой расстановкой данных
        boolean result3 = userDao.addFriend(user2Id, user1Id); // повторяем добавление уже существующего друга

        // проверяем утверждения
        assertThat(result3)
                .isEqualTo(false); // проверяем что метод не подтверждает добавление уже существующего друга DB
    }

    @Test
    void findAllFriends() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);
        userDao.save(user3);
        userDao.save(user4);
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();
        Long user3Id = user3.getId();
        Long user4Id = user4.getId();
        userDao.addFriend(user1Id, user2Id);
        userDao.addFriend(user3Id, user1Id);
        userDao.addFriend(user1Id, user4Id);

        // вызываем тестируемый метод
        List<User> result = userDao.findAllFriends(user1Id);

        // проверяем утверждения
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(2); // проверяем что у пользователя с d=1 два друга

        assertThat(result.get(0))
                .usingRecursiveComparison() // проверяем значения полей обновленного пользователя
                .ignoringFields("friends") // пропускаем проверку полей
                .isEqualTo(user2); // проверяем что первым первым идет друг с id=2
    }

    @Test
    void findCommonFriends() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);
        userDao.save(user3);
        userDao.save(user4);
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();
        Long user3Id = user3.getId();
        Long user4Id = user4.getId();
        userDao.addFriend(user2Id, user1Id);
        userDao.addFriend(user2Id, user3Id);
        userDao.addFriend(user2Id, user4Id);
        userDao.addFriend(user4Id, user1Id);
        userDao.addFriend(user3Id, user4Id);

        // вызываем тестируемый метод
        List<User> result = userDao.findCommonFriends(user2Id, user4Id);

        // проверяем утверждения
        assertThat(result.size())
                .isNotNull() // проверяем, что объект не равен null
                .isEqualTo(1); // проверяем что у пользователя с d=1 два друга

        assertThat(result.get(0))
                .usingRecursiveComparison() // проверяем значения полей обновленного пользователя
                .ignoringFields("friends") // пропускаем проверку полей
                .isEqualTo(user1); // проверяем что первым первым идет друг с id=2
    }

    @Test
    void deleteFriend() {

        // Подготавливаем данные для теста
        setUpUsers();
        userDao.save(user1);
        userDao.save(user2);
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();
        userDao.addFriend(user1Id, user2Id);

        // вызываем тестируемый метод
        boolean result = userDao.deleteFriend(user1Id, user2Id);

        // проверяем утверждения
        assertThat(result)
                .isEqualTo(true); // проверяем что метод подтверждает удаление дружбы из DB

        // повторно вызываем тестируемый метод
        boolean result2 = userDao.deleteFriend(user1Id, user2Id); // повторяем удаление уже удаленной дружбы

        // проверяем утверждения
        assertThat(result2)
                .isEqualTo(false); // проверяем что метод не подтверждает удаление несуществующего дружбы из DB

        // повторно вызываем тестируемый метод c другой расстановкой данных
        boolean result3 = userDao.deleteFriend(user2Id, user1Id); // повторяем удаление уже удаленной дружбы

        // проверяем утверждения
        assertThat(result3)
                .isEqualTo(false); // проверяем что метод не подтверждает удаление несуществующего дружбы из DB
    }
}