package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.NoteListDto;
import com.eightlow.decalcomanie.perfume.entity.NoteList;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface NoteListMapper extends BaseMapper<NoteList, NoteListDto> {
}
