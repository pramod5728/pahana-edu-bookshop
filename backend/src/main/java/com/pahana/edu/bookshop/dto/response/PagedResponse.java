package com.pahana.edu.bookshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paginated response wrapper for paginated API endpoints
 * 
 * Provides pagination metadata along with the data content
 * for endpoints that return paginated results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    private List<T> content;
    private PaginationInfo pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;
        private int numberOfElements;
    }

    /**
     * Create paginated response from Spring Data Page
     */
    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> page) {
        PaginationInfo pagination = PaginationInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .numberOfElements(page.getNumberOfElements())
                .build();

        return PagedResponse.<T>builder()
                .content(page.getContent())
                .pagination(pagination)
                .build();
    }

    /**
     * Create paginated response with custom content and pagination info
     */
    public static <T> PagedResponse<T> of(List<T> content, PaginationInfo pagination) {
        return PagedResponse.<T>builder()
                .content(content)
                .pagination(pagination)
                .build();
    }
}