package com.parma.user.service.handler;

import com.parma.common.dto.PageableResponse;
import com.parma.common.dto.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageableResponseHandlerService {
    public <T> PaginationResponse handlePaginationResponse(PageableResponse<T> pageResponse) {
        try {
            if (pageResponse == null) {
                log.warn("PageableResponse is null");
                return new PaginationResponse();
            }
            return new PaginationResponse(
                    pageResponse.getTotalPages(),
                    pageResponse.getPageNumber(),
                    pageResponse.getPageSize(),
                    pageResponse.getTotalElements(),
                    pageResponse.getNumberOfElements()
            );
        } catch (Exception e) {
            log.error("Error handling pagination response: {}", e.getMessage());
            throw e;
        }
    }
    public PaginationResponse handlePaginationResponse(int totalElements, int pageNumber, int pageSize) {
        try {
            int totalPages = calculateTotalPages(totalElements, pageSize);
            return new PaginationResponse(
                    totalPages,
                    pageNumber,
                    pageSize,
                    totalElements,
                    Math.min(pageSize, totalElements)
            );
        } catch (Exception e) {
            log.error("Error handling pagination response: {}", e.getMessage());
            throw e;
        }
    }
    private int calculateTotalPages(int totalElements, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }
}
