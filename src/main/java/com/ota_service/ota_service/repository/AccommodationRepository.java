package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Accommodation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    Optional<Accommodation> findByCode(String code);

    Optional<Accommodation> findFirstByNameAndAddress(String name, String address);

    @Query(
            value = """
                    SELECT a.*
                    FROM ACCOMMODATIONS a
                    WHERE a.business_status = 'OPEN'
                      AND (:regionId IS NULL OR a.region_id = :regionId)
                      AND (:accommodationType IS NULL OR a.accommodation_type = :accommodationType)
                      AND EXISTS (
                          SELECT 1
                          FROM ROOMS r
                          WHERE r.accommodation_id = a.id
                            AND r.active_yn = TRUE
                            AND (:bedType IS NULL OR r.bed_type = :bedType)
                            AND (:guestCount IS NULL OR r.max_occupancy >= :guestCount)
                            AND (
                                SELECT COUNT(*)
                                FROM ROOM_RATES rr
                                WHERE rr.room_id = r.id
                                  AND rr.active_yn = TRUE
                                  AND rr.rate_date BETWEEN :checkInDate AND :stayEndDate
                            ) = :stayNights
                            AND (
                                :minTotalAmount IS NULL OR (
                                    SELECT COALESCE(SUM(rr.sale_price), 0)
                                    FROM ROOM_RATES rr
                                    WHERE rr.room_id = r.id
                                      AND rr.active_yn = TRUE
                                      AND rr.rate_date BETWEEN :checkInDate AND :stayEndDate
                                ) >= :minTotalAmount
                            )
                            AND (
                                :maxTotalAmount IS NULL OR (
                                    SELECT COALESCE(SUM(rr.sale_price), 0)
                                    FROM ROOM_RATES rr
                                    WHERE rr.room_id = r.id
                                      AND rr.active_yn = TRUE
                                      AND rr.rate_date BETWEEN :checkInDate AND :stayEndDate
                                ) <= :maxTotalAmount
                            )
                            AND (
                                :excludeSoldOut = FALSE OR (
                                    SELECT COUNT(*)
                                    FROM ROOM_INVENTORIES ri
                                    WHERE ri.room_id = r.id
                                      AND ri.active_yn = TRUE
                                      AND ri.inventory_date BETWEEN :checkInDate AND :stayEndDate
                                      AND ri.stop_sale_yn = FALSE
                                      AND ri.available_stock > 0
                                ) = :stayNights
                            )
                      )
                    ORDER BY a.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM ACCOMMODATIONS a
                    WHERE a.business_status = 'OPEN'
                      AND (:regionId IS NULL OR a.region_id = :regionId)
                      AND (:accommodationType IS NULL OR a.accommodation_type = :accommodationType)
                      AND EXISTS (
                          SELECT 1
                          FROM ROOMS r
                          WHERE r.accommodation_id = a.id
                            AND r.active_yn = TRUE
                            AND (:bedType IS NULL OR r.bed_type = :bedType)
                            AND (:guestCount IS NULL OR r.max_occupancy >= :guestCount)
                            AND (
                                SELECT COUNT(*)
                                FROM ROOM_RATES rr
                                WHERE rr.room_id = r.id
                                  AND rr.active_yn = TRUE
                                  AND rr.rate_date BETWEEN :checkInDate AND :stayEndDate
                            ) = :stayNights
                            AND (
                                :minTotalAmount IS NULL OR (
                                    SELECT COALESCE(SUM(rr.sale_price), 0)
                                    FROM ROOM_RATES rr
                                    WHERE rr.room_id = r.id
                                      AND rr.active_yn = TRUE
                                      AND rr.rate_date BETWEEN :checkInDate AND :stayEndDate
                                ) >= :minTotalAmount
                            )
                            AND (
                                :maxTotalAmount IS NULL OR (
                                    SELECT COALESCE(SUM(rr.sale_price), 0)
                                    FROM ROOM_RATES rr
                                    WHERE rr.room_id = r.id
                                      AND rr.active_yn = TRUE
                                      AND rr.rate_date BETWEEN :checkInDate AND :stayEndDate
                                ) <= :maxTotalAmount
                            )
                            AND (
                                :excludeSoldOut = FALSE OR (
                                    SELECT COUNT(*)
                                    FROM ROOM_INVENTORIES ri
                                    WHERE ri.room_id = r.id
                                      AND ri.active_yn = TRUE
                                      AND ri.inventory_date BETWEEN :checkInDate AND :stayEndDate
                                      AND ri.stop_sale_yn = FALSE
                                      AND ri.available_stock > 0
                                ) = :stayNights
                            )
                      )
                    """,
            nativeQuery = true
    )
    Page<Accommodation> searchAvailableAccommodations(
            @Param("regionId") Long regionId,
            @Param("accommodationType") String accommodationType,
            @Param("bedType") String bedType,
            @Param("checkInDate") java.time.LocalDate checkInDate,
            @Param("stayEndDate") java.time.LocalDate stayEndDate,
            @Param("guestCount") Integer guestCount,
            @Param("minTotalAmount") java.math.BigDecimal minTotalAmount,
            @Param("maxTotalAmount") java.math.BigDecimal maxTotalAmount,
            @Param("stayNights") long stayNights,
            @Param("excludeSoldOut") boolean excludeSoldOut,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT a.*
                    FROM ACCOMMODATIONS a
                    LEFT JOIN RESERVATIONS rsv
                        ON rsv.accommodation_id = a.id
                       AND rsv.reservation_status IN ('CONFIRMED', 'COMPLETED')
                    WHERE a.business_status = 'OPEN'
                      AND (:accommodationType IS NULL OR a.accommodation_type = :accommodationType)
                      AND EXISTS (
                          SELECT 1
                          FROM ROOMS r
                          JOIN ROOM_RATES rr ON rr.room_id = r.id
                          WHERE r.accommodation_id = a.id
                            AND r.active_yn = TRUE
                            AND rr.active_yn = TRUE
                            AND rr.rate_date >= :baseDate
                      )
                    GROUP BY a.id
                    ORDER BY COUNT(rsv.id) DESC, a.id DESC
                    LIMIT 20
                    """,
            nativeQuery = true
    )
    java.util.List<Accommodation> findPopularAccommodations(
            @Param("accommodationType") String accommodationType,
            @Param("baseDate") java.time.LocalDate baseDate
    );
}
