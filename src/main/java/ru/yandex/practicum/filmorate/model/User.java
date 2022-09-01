package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    private Integer id;
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\s*\\w+\\s*$")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    @Setter(AccessLevel.NONE)
    private Set<Integer> friendList = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriend(Integer friendId) {
        friendList.add(friendId);
    }

    public void deleteFriend(Integer friendId) {
        friendList.remove(friendId);
    }

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
