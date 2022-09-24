package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private final Integer id;
    private final String name;

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre(Integer id) {
        this.id = id;
        this.name = null;
    }
}
