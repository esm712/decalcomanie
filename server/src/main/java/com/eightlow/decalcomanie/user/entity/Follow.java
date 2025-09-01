package com.eightlow.decalcomanie.user.entity;

import com.eightlow.decalcomanie.user.dto.FollowDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@IdClass(FollowDto.class)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "follow")
public class Follow {
    @Id
    @Column(name="following")
    private String following;

    @Id
    @Column(name="followed")
    private String followed;
}
