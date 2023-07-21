package com.eightlow.decalcomanie.perfume.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PerfumeDto {
    private int perfumeId;

    private String name;

    private String nameOrg;

    private int brandId;

    private String picture;

    private int gender;

    private Float rate;

    private float longevity;

    private float sillage;

    private List<ScentDto> accord;

    private List<NoteListDto> note;
}
