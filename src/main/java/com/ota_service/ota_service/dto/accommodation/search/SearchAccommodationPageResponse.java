package com.ota_service.ota_service.dto.accommodation.search;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder(access = AccessLevel.PRIVATE)
public record SearchAccommodationPageResponse(
        List<SearchAccommodationItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static SearchAccommodationPageResponse from(Page<?> page, List<SearchAccommodationItemResponse> content) {
        return SearchAccommodationPageResponse.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
