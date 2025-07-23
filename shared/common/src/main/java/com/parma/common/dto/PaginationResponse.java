package com.parma.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaginationResponse(
        @JsonProperty("total_pages") int totalPage,
        @JsonProperty("current") int current,
        @JsonProperty("size") int size,
        @JsonProperty("records") long records,
        @JsonProperty("number_of_element") int numberOfElement
) {}
