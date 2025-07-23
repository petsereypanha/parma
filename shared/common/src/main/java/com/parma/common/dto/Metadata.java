package com.parma.common.dto;

import lombok.Builder;

@Builder
public record Metadata(
        boolean hasNext,
        long totalUsers,
        boolean hasPrevious,
        int currentPage,
        int pageSize
) {}