package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.dto.extranet.accommodation.ExtranetAccommodationSummaryResponse;
import com.ota_service.ota_service.entity.ExtranetAccommodation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExtranetAccommodationRepository extends JpaRepository<ExtranetAccommodation, Long> {

    boolean existsByExtranetIdAndAccommodationId(Long extranetId, Long accommodationId);

    Optional<ExtranetAccommodation> findByExtranetIdAndCode(Long extranetId, String code);

    @Query("""
            select new com.ota_service.ota_service.dto.extranet.accommodation.ExtranetAccommodationSummaryResponse(
                a.code,
                a.name,
                a.address,
                a.thumbnailImage,
                a.businessStatus
            )
            from ExtranetAccommodation sa
            inner join Accommodation a on a.id = sa.accommodationId
            where sa.extranetId = :extranetId
            order by sa.createdAt desc, sa.id desc
            """)
    List<ExtranetAccommodationSummaryResponse> findSummariesByExtranetId(@Param("extranetId") Long extranetId);
}
