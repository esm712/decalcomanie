package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.NoteListDto;
import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.entity.Accord;
import com.eightlow.decalcomanie.perfume.entity.NoteList;
import com.eightlow.decalcomanie.perfume.entity.Perfume;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PerfumeMapper {

    default PerfumeDto toDto(Perfume perfume) {
        List<ScentDto> accords = new ArrayList<>();
        List<NoteListDto> noteListDtoList = new ArrayList<>();

        if(perfume.getAccord() != null) {
            for (Accord a : perfume.getAccord()) {
                ScentDto sdto = ScentDto.builder()
                        .weight(a.getWeight())
                        .rgb(a.getScent().getRgb())
                        .scentId(a.getScent().getScentId())
                        .nameOrg(a.getScent().getNameOrg())
                        .name(a.getScent().getName())
                        .build();

                accords.add(sdto);
            }
        }

        if(perfume.getNote() != null) {
            for (NoteList noteList : perfume.getNote()) {
                NoteListDto nldto = NoteListDto.builder()
                        .noteId(noteList.getNote().getNoteId())
                        .noteListId(noteList.getNoteListId())
                        .noteName(noteList.getNote().getName())
                        .perfumeId(noteList.getPerfume().getPerfumeId())
                        .type(noteList.getType())
                        .build();

                noteListDtoList.add(nldto);
            }
        }

        PerfumeDto pdto = PerfumeDto.builder()
                .perfumeId(perfume.getPerfumeId())
                .name(perfume.getName())
                .nameOrg(perfume.getNameOrg())
                .brandId(perfume.getBrand().getBrandId())
                .brandName(perfume.getBrand().getName())
                .brandNameOrg(perfume.getBrand().getNameOrg())
                .picture(perfume.getPicture())
                .gender(perfume.getGender())
                .rate(perfume.getRate())
                .longevity(perfume.getLongevity())
                .sillage(perfume.getSillage())
                .pick(perfume.getPick())
                .accord(accords)
                .note(noteListDtoList)
                .spring(perfume.getSpring())
                .summer(perfume.getSummer())
                .fall(perfume.getFall())
                .winter(perfume.getWinter())
                .day(perfume.getDay())
                .night(perfume.getNight())
                .build();

        return pdto;
    }

    Perfume toEntity(PerfumeDto perfumeDto);

    default List<PerfumeDto> toDtoList(List<Perfume> perfumeList) {
        List<PerfumeDto> perfumeDtoList = new ArrayList<>();

        for (Perfume perfume : perfumeList) {
           perfumeDtoList.add(toSimpleDto(perfume));
        }

        return perfumeDtoList;
    }

    List<Perfume> toEntityList(List<PerfumeDto> perfumeDtoList);

    default PerfumeDto toSimpleDto(Perfume perfume) {
        List<ScentDto> accords = new ArrayList<>();

        if(perfume.getAccord() != null) {
            for (Accord a : perfume.getAccord()) {
                ScentDto sdto = ScentDto.builder()
                        .weight(a.getWeight())
                        .rgb(a.getScent().getRgb())
                        .scentId(a.getScent().getScentId())
                        .nameOrg(a.getScent().getNameOrg())
                        .name(a.getScent().getName())
                        .build();

                accords.add(sdto);
            }
        }

        PerfumeDto pdto = PerfumeDto.builder()
                .perfumeId(perfume.getPerfumeId())
                .nameOrg(perfume.getNameOrg())
                .name(perfume.getName())
                .brandNameOrg(perfume.getBrand().getNameOrg())
                .brandName(perfume.getBrand().getName())
                .picture(perfume.getPicture())
                .pick(perfume.getPick())
                .rate(perfume.getRate())
                .accord(accords)
                .build();

        return pdto;
    }


}
