package com.ota_service.ota_service.entity;

import com.ota_service.ota_service.enums.AccommodationRegionType;
import com.ota_service.ota_service.enums.AccommodationSourceType;
import com.ota_service.ota_service.enums.AccommodationType;
import com.ota_service.ota_service.enums.BusinessStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACCOMMODATIONS")
@Getter
@Setter
public class Accommodation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private AccommodationSourceType sourceType;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "external_product_id", length = 100)
    private String externalProductId;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_type", nullable = false)
    private AccommodationRegionType regionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodation_type", nullable = false)
    private AccommodationType accommodationType;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "thumbnail_image", length = 255)
    private String thumbnailImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_status", nullable = false)
    private BusinessStatus businessStatus;

    @Column(name = "check_in_time", nullable = false)
    private LocalTime checkInTime;

    @Column(name = "check_out_time", nullable = false)
    private LocalTime checkOutTime;
}
