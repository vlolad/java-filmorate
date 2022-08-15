package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class User {

    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        User otherUser = (User) obj;
        return Objects.equals(email, otherUser.email);
    }
}
