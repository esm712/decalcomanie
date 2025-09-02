package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.entity.Scent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScentMapper {
    Scent toEntity(ScentDto scentDto);

    ScentDto toDto(Scent scent);

    List<Scent> toEntity(List<ScentDto> scentDtoList);

    List<ScentDto> toDto(List<Scent> scentList);
}
