package com.ota_service.ota_service.dto.extranet.reservation;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder(access = AccessLevel.PRIVATE)
public record ExtranetReservationPageResponse(
        List<ExtranetReservationItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static ExtranetReservationPageResponse empty(Pageable pageable) {
        return ExtranetReservationPageResponse.builder()
                .content(List.of())
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();
    }

    public static ExtranetReservationPageResponse of(
            List<ExtranetReservationItemResponse> content,
            Pageable pageable,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
        return ExtranetReservationPageResponse.builder()
                .content(content)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(first)
                .last(last)
                .build();
    }
}
