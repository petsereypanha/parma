package com.parma.common.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {
    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    private int pageNumber;

    @Min(value = 1, message = "Page size must be greater than 0")
    private int pageSize;

    private String sortBy;
    private boolean desc;

    public int getOffset() {
        return pageNumber * pageSize;
    }

    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    public Sort.Direction getSortDirection() {
        return desc ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    public Sort getSort() {
        if (!hasSorting()) {
            return Sort.unsorted();
        }
        return Sort.by(getSortDirection(), sortBy);
    }

    public static PageableRequest of(int pageNumber, int pageSize) {
        return PageableRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }

    public static PageableRequest of(int pageNumber, int pageSize, String sortBy, boolean desc) {
        return PageableRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .desc(desc)
                .build();
    }

    public static PageableRequest firstPage(int pageSize) {
        return of(0, pageSize);
    }

    public static PageableRequest firstPage(int pageSize, String sortBy, boolean desc) {
        return of(0, pageSize, sortBy, desc);
    }

    public PageableRequest nextPage() {
        return PageableRequest.builder()
                .pageNumber(pageNumber + 1)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .desc(desc)
                .build();
    }

    public PageableRequest previousPage() {
        if (pageNumber <= 0) {
            return this;
        }
        return PageableRequest.builder()
                .pageNumber(pageNumber - 1)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .desc(desc)
                .build();
    }

    public PageableRequest withSort(String sortBy, boolean desc) {
        return PageableRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .desc(desc)
                .build();
    }

    public PageableRequest withPageSize(int newPageSize) {
        return PageableRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(newPageSize)
                .sortBy(sortBy)
                .desc(desc)
                .build();
    }
}
