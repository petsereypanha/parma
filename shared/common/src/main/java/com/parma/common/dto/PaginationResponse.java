package com.parma.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse {

    @JsonProperty("total_pages")
    private int totalPage;

    @JsonProperty("current")
    private int current;

    @JsonProperty("size")
    private int size;

    @JsonProperty("records")
    private long records;

    @JsonProperty("number_of_element")
    private int numberOfElement;
}
