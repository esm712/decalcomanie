package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.NoteListDto;
import com.eightlow.decalcomanie.perfume.entity.NoteList;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface NoteListMapper{
    NoteListDto toDto(NoteList noteList);
    NoteList toEntity(NoteListDto noteListDto);

    List<NoteListDto> toDtoList(List<NoteList> noteLists);
    List<NoteList> toEntityList(List<NoteListDto> noteListDtos);
}
