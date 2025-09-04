package com.eightlow.decalcomanie.common.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class BaseDto {
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
