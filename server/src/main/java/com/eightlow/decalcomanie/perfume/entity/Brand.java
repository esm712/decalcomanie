package com.eightlow.decalcomanie.perfume.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "brand")
public class Brand {

    @Id
    @Column(name = "brandId")
    private int brandId;

    @Column(name = "nameOrg")
    private String nameOrg;

    @Column(name = "name")
    private String name;
}
