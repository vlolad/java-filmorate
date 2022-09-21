package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface RatingsDao {

    MPA getMpaById(Integer mpaId);
    List<MPA> getAllMpa();
}
