package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.NoteDto;
import com.eightlow.decalcomanie.perfume.entity.Note;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface NoteMapper{
    NoteDto toDto(Note note);
    Note toEntity(NoteDto noteDto);

    List<NoteDto> toDtoList(List<Note> notes);
    List<Note> toEntiyList(List<NoteDto> noteDtos);
}
