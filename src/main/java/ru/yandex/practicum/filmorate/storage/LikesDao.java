package ru.yandex.practicum.filmorate.storage;

public interface LikesDao {
    void addLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
}
