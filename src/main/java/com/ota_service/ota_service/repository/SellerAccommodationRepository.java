package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.dto.seller.accommodation.SellerAccommodationSummaryResponse;
import com.ota_service.ota_service.entity.SellerAccommodation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SellerAccommodationRepository extends JpaRepository<SellerAccommodation, Long> {

    boolean existsBySellerIdAndAccommodationId(Long sellerId, Long accommodationId);

    Optional<SellerAccommodation> findBySellerIdAndCode(Long sellerId, String code);

    @Query("""
            select new com.ota_service.ota_service.dto.seller.accommodation.SellerAccommodationSummaryResponse(
                a.code,
                a.name,
                a.address,
                a.thumbnailImage,
                a.businessStatus
            )
            from SellerAccommodation sa
            inner join Accommodation a on a.id = sa.accommodationId
            where sa.sellerId = :sellerId
            order by sa.createdAt desc, sa.id desc
            """)
    List<SellerAccommodationSummaryResponse> findSummariesBySellerId(@Param("sellerId") Long sellerId);
}
