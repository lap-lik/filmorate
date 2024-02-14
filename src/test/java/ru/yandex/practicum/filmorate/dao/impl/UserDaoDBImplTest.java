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
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDaoDBImplTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User userUpdate;

    @BeforeEach
    void setUp() {

        userDao = new UserDaoDBImpl(jdbcTemplate);
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


    @Test
    void testSaveUserWithExpectedResultNotNull() {

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
    void testSaveUserWithInvalidEmailResultException() {

        // Подготавливаем данные для теста
        user1.setEmail("mail mail.ru"); //устанавливаем не правильный email

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> userDao.save(user1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the user in the DB."); // проверяем текст ошибки
    }

    @Test
    void testSaveUserWithInvalidLoginResultException() {

        // Подготавливаем данные для теста
        user1.setLogin("dol ore"); //устанавливаем логин с пробелом

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> userDao.save(user1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the user in the DB."); // проверяем текст ошибки
    }

    @Test
    void testSaveUserWithInvalidBirthdayResultException() {

        // Подготавливаем данные для теста
        user1.setBirthday(LocalDate.of(2025, 1, 1)); //устанавливаем дату рождения в будущем

        // вызываем тестируемый метод и проверяем утверждения
        Throwable exception = assertThrows(SQLDataAccessException.class,
                () -> userDao.save(user1)); // проверяем, что DB выбросит исключение SQLDataAccessException
        assertEquals(exception.getMessage(), "Error saving the user in the DB."); // проверяем текст ошибки
    }

    @Test
    void testFindUserByIdWithExpectedResultNotNull() {

        // Подготавливаем данные для теста
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
    void testFindUserByInvalidIdResultNull() {

        // Подготавливаем данные для теста
        userDao.save(user1);
        userDao.save(user2);

        // вызываем тестируемый метод
        User result = userDao.findById(999L).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет пользователя по ID = 999
    }

    @Test
    void testFindAllResultListOfUsers() {

        // Подготавливаем данные для теста
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
    void testUpdateUserWithExpectedResultNotNull() {

        // Подготавливаем данные для теста
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
    void testUpdateUserWithInvalidUserIdResultNull() {

        // вызываем тестируемый метод
        userDao.save(user1);
        userUpdate.setId(999L);

        // вызываем тестируемый метод
        User result = userDao.update(userUpdate).orElse(null);

        // проверяем утверждения
        assertThat(result)
                .isNull(); // проверяем, что возвращает null так как в DB нет пользователя с ID = 999
    }

    @Test
    void testDeleteByUserIdResultUserDeleted() {

        // Подготавливаем данные для теста
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
    void testUserExistenceByIdResultTrue() {

        // Подготавливаем данные для теста
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
    void testAddingFriendWhitIdUserAndFriendResultTrue() {

        // Подготавливаем данные для теста
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
    void testFindAllFriendsResultListOfUsers() {

        // Подготавливаем данные для теста
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
    void testFindCommonFriendsResultListOfUsers() {

        // Подготавливаем данные для теста
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
    void testDeleteFriendByUserIdAndFriendIdResultDeletedLinkUserFriend() {

        // Подготавливаем данные для теста
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