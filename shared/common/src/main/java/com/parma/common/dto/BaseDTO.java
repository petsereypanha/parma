package com.parma.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record BaseDTO(
    @JsonProperty("created_at") LocalDateTime createdAt,
    @JsonProperty("created_by") String createdBy,
    @JsonProperty("updated_at") LocalDateTime updatedAt,
    @JsonProperty("updated_by") String updatedBy
) {
    public BaseDTO(BaseDTO baseDTO) {
        this(baseDTO.createdAt(), baseDTO.createdBy(), baseDTO.updatedAt(), baseDTO.updatedBy());
    }
}