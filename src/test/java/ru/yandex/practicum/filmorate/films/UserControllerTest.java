package ru.yandex.practicum.filmorate.films;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    static{
        Locale.setDefault(new Locale("en"));
    }
    private UserController controller;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private final User user1 = new User("vlad19.99@yandex.ru", "vlolad", "Влдад",
            LocalDate.of(1999, 9, 10));
    private final User user2 = new User("maria@yandex.ru", "mash0", "Мария",
            LocalDate.of(2000, 2, 28));

    @BeforeEach
    public void createController() {
        controller = new UserController();
    }

    @Test
    public void testEmptyUserList() {
        assertTrue(controller.findAll().isEmpty(),
                "Ожидался пустой список при создании нового контроллера.");
    }

    @Test
    public void testCreateUser() {
        controller.create(user1);
        assertTrue(controller.findAll().contains(user1),
                "Ошибка при создании пользователя.");
        assertEquals(1, controller.findAll().get(0).getId(),
                "Пользователю неверно присваивается id при добавлении.");
        controller.create(user2);
        assertEquals(2, controller.findAll().get(1).getId(),
                "Пользователю неверно присваивается id при добавлении в непустой список.");
    }

    @Test
    public void testUpdateUser() {
        controller.create(user1);
        User updUser1 = new User(1, "vasergeev_2@edu.hse.ru", "UpdatedVlad", "Владислав",
                LocalDate.of(1999, 9, 11));
        controller.update(updUser1);
        assertTrue(controller.findAll().contains(updUser1),
                "Ошибка при обновлении пользователя.");
    }

    @Test
    public void testWrongEmail() {
        User user3 = user1;
        user3.setEmail("What?");
        Set<ConstraintViolation<User>> violations = validator.validate(user3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must be a well-formed email address", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
        user3.setEmail("a.r?u@");
        Set<ConstraintViolation<User>> violations2 = validator.validate(user3);
        assertEquals(1, violations2.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must be a well-formed email address", violations2.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testLoginWithSpace() {
        User user3 = user1;
        user3.setLogin("Vlad olad");
        Set<ConstraintViolation<User>> violations = validator.validate(user3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must match \"^\\s*\\w+\\s*$\"", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testEmptyNameUser() {
        User user3 = user1;
        user3.setName(null);
        controller.create(user3);
        assertEquals("vlolad", controller.findAll().get(0).getName(),
                "Неправильное присвоение значения полю name, если оно пустое.");
    }

    @Test
    public void testTimeTravellerUser() {
        User user3 = user1;
        user3.setBirthday(LocalDate.of(2112, 2,2));
        Set<ConstraintViolation<User>> violations = validator.validate(user3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must be a date in the past or in the present", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testBornTomorrowUser() {
        User user3 = user1;
        user3.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must be a date in the past or in the present", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testUpdateUnknownUser() {
        User user3 = user1;
        user3.setId(-1);
        NotFoundException exc = assertThrows(NotFoundException.class, () -> controller.update(user3),
                "Ожидалось исключение NotFoundException");
        assertEquals("404 NOT_FOUND \"User not found.\"", exc.getMessage());
    }
}
