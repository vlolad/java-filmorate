package ru.yandex.practicum.filmorate.films;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    static{
        Locale.setDefault(new Locale("en"));
    }
    private FilmController controller;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private final Film test1 = new Film("Никто", "(Не)простой боевик",
            LocalDate.of(2018,11,3), 110);
    private final Film test2 = new Film("Фильм 2", "Провалился в прокате, но тоже хороший",
            LocalDate.of(2016,9,27), 97);

    @BeforeEach
    public void createController(){
        controller = new FilmController();
    }

    @Test
    public void testEmptyFilmList() {
        assertTrue(controller.findAll().isEmpty(),
        "Список фильмов не пустой при создании контроллера.");
    }

    @Test
    public void testCreateFilm() {
        controller.create(test1);
        assertTrue(controller.findAll().contains(test1),
                "Ошибка при добавлении фильма.");
        assertEquals(1, controller.findAll().get(0).getId(),
                "Фильму неверно присваивается id при добавлении.");
        controller.create(test2);
        assertEquals(2, controller.findAll().get(1).getId(),
                "Фильму неверно присваивается id при добавлении в непустой список.");
    }

    @Test
    public void testUpdateFilm() {
        controller.create(test1);
        Film updTest1 = new Film(1, "Никто 2", "Теперь это комедия", LocalDate.now(), 67L);
        controller.update(updTest1);
        assertTrue(controller.findAll().contains(updTest1),
                "Ошибка при обновлении фильма.");
    }

    @Test
    public void testCreateNoNameFilm() {
        Film test3 = test1;
        test3.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(test3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must not be blank", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testCreateTooLongDescription() {
        Film test3 = test1;
        test3.setDescription("Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, " +
                "который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «своего отсутствия», стал кандидатом Коломбани."); // 243 symbols
        Set<ConstraintViolation<Film>> violations = validator.validate(test3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("size must be between 0 and 200", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testCreate200LengthDescription() {
        Film test3 = test1;
        test3.setDescription("Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, " +
                "который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «сво"); // 200 symbols
        Set<ConstraintViolation<Film>> violations = validator.validate(test3);
        assertEquals(0, violations.size(),
                "Присутствует ошибка валидации, хотя не ожидалась.");
    }

    @Test
    public void testCreate201LengthDescription() {
        Film test3 = test1;
        test3.setDescription("Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, " +
                "который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «свое"); // 201 symbols
        Set<ConstraintViolation<Film>> violations = validator.validate(test3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("size must be between 0 and 200", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testCreateTooOldFilm() {
        Film test3 = test1;
        test3.setReleaseDate(LocalDate.of(1799, 2, 1));
        ValidationException exc = assertThrows(ValidationException.class, () -> controller.create(test3),
                "Ожидалось исключение ValidationException");
        assertEquals("400 BAD_REQUEST \"Film is unusually old.\"", exc.getMessage());
    }

    @Test
    public void testCreateNegativeDuration() {
        Film test3 = test1;
        test3.setDuration(-1L);
        Set<ConstraintViolation<Film>> violations = validator.validate(test3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must be greater than 0", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testCreateZeroDuration() {
        Film test3 = test1;
        test3.setDuration(0L);
        Set<ConstraintViolation<Film>> violations = validator.validate(test3);
        assertEquals(1, violations.size(),
                "Отсутствует ошибка валидации.");
        assertEquals("must be greater than 0", violations.iterator().next().getMessage(),
                "Ожидалась иная ошибка валидации.");
    }

    @Test
    public void testUpdateUnknownFilm() {
        controller.create(test1);
        Film test3 = test1;
        test3.setId(-1);
        NotFoundException exc = assertThrows(NotFoundException.class, () -> controller.update(test3),
                "Ожидалось исключение NotFoundException");
        assertEquals("404 NOT_FOUND \"Film not found.\"", exc.getMessage());
    }
}
