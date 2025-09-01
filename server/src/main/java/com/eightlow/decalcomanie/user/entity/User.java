package com.eightlow.decalcomanie.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @Column(name="userId")
    private String userId;

    @Column(name="nickname")
    private String nickname;

    @Column(name="deletedAt")
    private LocalDateTime deletedAt;

    @Column(name="age")
    private int age;

    @Column(name="gender")
    private int gender;

    @Column(name="picture")
    private String picture;

    @OneToMany(mappedBy = "user")
    private List<UserPerfume> userPerfume;

    @OneToMany(mappedBy = "user")
    private List<UserScent> userScent;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePicture(String picture) {
        this.picture = picture;
    }
}
