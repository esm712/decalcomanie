package com.eightlow.decalcomanie.auth.entity;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "usercredential")
public class UserCredential {
    @Id
    @Column(name="userId")
    private String userId;

    @Column(name="email")
    private String email;

    @Column(name="refreshToken")
    private String refreshToken;

    @Column(name="kakaoUserNum")
    private String kakaoUserNum;
}
