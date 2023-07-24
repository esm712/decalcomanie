package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.perfume.dto.NoteDto;
import com.eightlow.decalcomanie.perfume.dto.NoteListDto;
import com.eightlow.decalcomanie.perfume.entity.NoteList;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface NoteListMapper {
    NoteList toEntity(NoteListDto noteListDto);

    NoteListDto toDto(NoteList noteList);

    List<NoteList> toEntity(List<NoteListDto> noteListDtoList);

    List<NoteListDto> toDto(List<NoteList> noteListList);
}
