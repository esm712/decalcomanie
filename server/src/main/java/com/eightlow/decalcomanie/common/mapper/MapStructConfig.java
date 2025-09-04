package com.eightlow.decalcomanie.common.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지 않는 필드 무시
)
public class MapStructConfig {
}
