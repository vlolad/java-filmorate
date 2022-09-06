package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

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
    private Map<Integer, Boolean> friendList = new HashMap<>();

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

    public void addFriend(Integer friendId, Boolean acceptance) {
        friendList.put(friendId, acceptance);
    }

    public void deleteFriend(Integer friendId) {
        friendList.remove(friendId);
    }

    //TODO на момент тестов ТЗ-11 оставил вывод всех друзей, надо вывод только подтвержденных
    public void acceptFriend(Integer friendId) {
        friendList.replace(friendId, true);
    }

    public Set<Integer> getFriendList() {
        return friendList.keySet();
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
