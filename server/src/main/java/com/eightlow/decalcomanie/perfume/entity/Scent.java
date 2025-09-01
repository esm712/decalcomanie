package com.eightlow.decalcomanie.perfume.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scent")
public class Scent {

    @Id
    @Column(name = "scentId")
    private int scentId;

    @Column(name = "nameOrg")
    private String nameOrg;

    @Column(name = "rgb")
    private String rgb;

    @Column(name = "name")
    private String name;
}
