package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.perfume.dto.NoteDto;
import com.eightlow.decalcomanie.perfume.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(config = MapperConfig.class)
public interface NoteMapper extends BaseMapper<Note, NoteDto> {
}
