package ru.yandex.practicum.filmorate.model;

import lombok.*;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Film {

    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotBlank
    private String name;
    @Size(max=200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private long duration;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public LocalDate getMinReleaseDate() {
        return MIN_RELEASE_DATE;
    }
}
