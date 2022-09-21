package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MPA {
    private final Integer id;
    private final String name;

    public MPA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public MPA(Integer id) {
        this.id = id;
        this.name = null;
    }
}
