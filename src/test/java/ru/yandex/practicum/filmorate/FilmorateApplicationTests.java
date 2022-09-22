package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenresDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.RatingsDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenresDaoImpl genreStorage;
    private final RatingsDaoImpl ratingsStorage;

    @Test
    @Order(1)
    public void testCreateAndFindUser() {
        User testUser = userStorage.createUser(new User("test@yandex.ru", "testName",
                "testDesc", LocalDate.of(2019, 1, 1)));
        Integer generatedId = testUser.getId();
        assertEquals(1, generatedId,
                "Expected ID=1, get ID=" + generatedId + " on created user.");
        User user = userStorage.getUser(1);
        assertEquals(1, (int) user.getId(),
                "Expected ID=1, get ID=" + user.getId() + " on found user.");
    }

    @Test
    public void testGetUserByNegativeId() {
        assertThrows(NotFoundException.class, () -> userStorage.getUser(-1));
    }

    @Test
    @Order(2)
    public void testUpdateUser() {
        User user = userStorage.getUser(1);
        user.setName("UpdatedUser");
        userStorage.updateUser(user);
        User testUser = userStorage.getUser(1);
        assertEquals("UpdatedUser", testUser.getName(),
                "Expected new name in updated user, gets old name.");
    }

    @Test
    @Order(3)
    public void testCreateAnotherUsers() {
        User testUser = userStorage.createUser(new User("second@yandex.ru", "secondUser",
                "testDesc", LocalDate.of(2017, 1, 1)));
        User testUser2 = userStorage.createUser(new User("heh@mail.ru", "justUser",
                "testDesc", LocalDate.of(2015, 1, 1)));
        Integer generatedId = testUser.getId();
        assertEquals(2, generatedId,
                "Expected ID=2, get ID=" + generatedId + " on created user.");
        Integer generatedId2 = testUser2.getId();
        assertEquals(3, generatedId2,
                "Expected ID=3, get ID=" + generatedId2 + " on created user.");

    }

    @Test
    @Order(4)
    public void testGetAllUsers() {
        List<User> users = userStorage.getUsers();
        assertEquals(3, users.size(),
                "Wrong users amount: expected=3, get=" + users.size());
    }

    @Test
    @Order(5)
    public void testAddFriends() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 3);
        List<User> userOneFriends = userStorage.getFriendsByUserId(1);
        assertEquals(2, userOneFriends.size(),
                "Wrong amount of friends: expected=2, got=" + userOneFriends.size());
    }

    @Test
    @Order(6)
    public void testCommonFriends() {
        userStorage.addFriend(2, 3);
        List<User> commonFriends = userStorage.getCommonFriends(1, 2);
        assertEquals(1, commonFriends.size(),
                "Wrong amount of common friends: expected=1, got=" + commonFriends.size());
        assertEquals(3, commonFriends.get(0).getId(),
                "Wrong common friend id: expected=3, got=" + commonFriends.get(0).getId());
    }

    @Test
    @Order(7)
    public void testDeleteFriend() {
        userStorage.deleteFriend(1, 3);
        List<User> commonFriends = userStorage.getCommonFriends(1, 2);
        assertEquals(0, commonFriends.size(),
                "Wrong amount of common friends: expected=0, got=" + commonFriends.size());
    }

    @Test
    public void getAllGenres() {
        List<Genre> genresList = genreStorage.getAllGenres();
        assertEquals(6, genresList.size(),
                "Wrong amount of genres: expected=6, got=" + genresList.size());
    }

    @Test
    public void getGenreById() {
        Genre genre = genreStorage.getGenreById(1);
        assertEquals("Комедия", genre.getName(),
                "Unexpected genre name: need=Комедия, got=" + genre.getName());
    }

    @Test
    public void getAllMpa() {
        List<MPA> ratings = ratingsStorage.getAllMpa();
        assertEquals(5, ratings.size(),
                "Wrong amount of ratings: expected=5, got=" + ratings.size());
    }

    @Test
    public void getMpaById() {
        MPA rating = ratingsStorage.getMpaById(1);
        assertEquals("G", rating.getName(),
                "Unexpected rating name: need=G, got=" + rating.getName());
    }

    @Test
    @Order(8)
    public void testCreateFilms() {
        Film film1 = new Film("film1", "just film", LocalDate.of(1999, 5, 9),
                69, new MPA(5), (List.of(new Genre(3), new Genre(4))));
        Film film2 = new Film("film2", "just sequel", LocalDate.of(2001, 9, 9),
                69, new MPA(3), (List.of(new Genre(1), new Genre(6))));
        Integer generatedId1 = filmStorage.createFilm(film1).getId();
        Integer generatedId2 = filmStorage.createFilm(film2).getId();
        List<Film> films = filmStorage.getFilms();

        assertEquals(2, films.size(),
                "Too much films created in DB: expected=2, got=" + films.size());
        assertEquals(1, generatedId1);
        assertEquals(2, generatedId2);
    }

    @Test
    @Order(9)
    public void testGetFilmById() {
        Film testFilm = filmStorage.getFilm(2);
        assertEquals("film2", testFilm.getName());
    }

    @Test
    @Order(10)
    public void testUpdateFilm() {
        Film film = filmStorage.getFilm(1);
        film.setName("Ogr");
        filmStorage.updateFilm(film);
        Film testFilm = filmStorage.getFilm(1);
        assertEquals("Ogr", testFilm.getName());
    }

    @Test
    @Order(11)
    public void testUpdateFilmButRepeatGenres() {
        Film film = filmStorage.getFilm(2);
        film.setGenres(List.of(new Genre(1), new Genre(4), new Genre(1), new Genre(4)));
        filmStorage.updateFilm(film);
        Film testFilm = filmStorage.getFilm(2);
        assertEquals(2, testFilm.getGenres().size());
    }

    @Test
    @Order(12)
    public void testUpdateFilmButEmptyGenres() {
        Film film = filmStorage.getFilm(1);
        List<Genre> newGenres = new ArrayList<>();
        film.setGenres(newGenres);
        filmStorage.updateFilm(film);
        Film testFilm = filmStorage.getFilm(1);
        assertTrue(testFilm.getGenres().isEmpty());
    }

    @Test
    @Order(13)
    public void testAddLikes() {
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(2, 3);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 3);
        Integer film1Rate = filmStorage.getFilm(1).getRate();
        assertEquals(2, film1Rate);
        Integer film2Rate = filmStorage.getFilm(2).getRate();
        assertEquals(3, film2Rate);

    }

    @Test
    @Order(14)
    public void testGetPopular() {
        List<Film> popularFilms = filmStorage.getPopular(2);
        assertEquals(2, popularFilms.size());
        assertEquals(2, popularFilms.get(0).getId());
    }

    @Test
    @Order(15)
    public void testDeleteLike() {
        filmStorage.deleteLike(2, 1);
        filmStorage.deleteLike(2, 3);
        List<Film> popularFilms = filmStorage.getPopular(2);
        assertEquals(1, popularFilms.get(0).getId());
    }
}
