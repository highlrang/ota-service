package com.ota_service.ota_service.dto.accommodation.search;

import java.util.List;

public record SearchAccommodationPageResponse(
        List<SearchAccommodationItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
