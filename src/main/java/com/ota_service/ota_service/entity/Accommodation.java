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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ACCOMMODATIONS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private AccommodationSourceType sourceType;

    @Column(name = "extranet_id")
    private Long extranetId;

    @Column(name = "supplier_product_id", length = 100)
    private String supplierProductId;

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

    public static Accommodation createExtranet(
            Long extranetId,
            String name,
            AccommodationRegionType regionType,
            AccommodationType accommodationType,
            Long regionId,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String thumbnailImage,
            LocalTime checkInTime,
            LocalTime checkOutTime,
            String temporaryCode
    ) {
        Accommodation accommodation = new Accommodation();
        accommodation.sourceType = AccommodationSourceType.EXTRANET;
        accommodation.extranetId = extranetId;
        accommodation.supplierProductId = null;
        accommodation.name = name;
        accommodation.regionType = regionType;
        accommodation.accommodationType = accommodationType;
        accommodation.regionId = regionId;
        accommodation.address = address;
        accommodation.latitude = latitude;
        accommodation.longitude = longitude;
        accommodation.thumbnailImage = thumbnailImage;
        accommodation.businessStatus = BusinessStatus.PENDING_APPROVAL;
        accommodation.checkInTime = checkInTime;
        accommodation.checkOutTime = checkOutTime;
        accommodation.code = temporaryCode;
        return accommodation;
    }

    public static Accommodation createSupplier(
            String name,
            AccommodationRegionType regionType,
            AccommodationType accommodationType,
            Long regionId,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String thumbnailImage,
            LocalTime checkInTime,
            LocalTime checkOutTime,
            String temporaryCode
    ) {
        Accommodation accommodation = new Accommodation();
        accommodation.sourceType = AccommodationSourceType.SUPPLIER;
        accommodation.extranetId = null;
        accommodation.supplierProductId = null;
        accommodation.name = name;
        accommodation.regionType = regionType;
        accommodation.accommodationType = accommodationType;
        accommodation.regionId = regionId;
        accommodation.address = address;
        accommodation.latitude = latitude;
        accommodation.longitude = longitude;
        accommodation.thumbnailImage = thumbnailImage;
        accommodation.businessStatus = BusinessStatus.OPEN;
        accommodation.checkInTime = checkInTime;
        accommodation.checkOutTime = checkOutTime;
        accommodation.code = temporaryCode;
        return accommodation;
    }

    public void updateSupplier(
            String name,
            AccommodationRegionType regionType,
            AccommodationType accommodationType,
            Long regionId,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String thumbnailImage,
            LocalTime checkInTime,
            LocalTime checkOutTime
    ) {
        this.name = name;
        this.regionType = regionType;
        this.accommodationType = accommodationType;
        this.regionId = regionId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnailImage = thumbnailImage;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public void updateExtranet(
            String name,
            AccommodationRegionType regionType,
            AccommodationType accommodationType,
            Long regionId,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String thumbnailImage,
            LocalTime checkInTime,
            LocalTime checkOutTime
    ) {
        this.name = name;
        this.regionType = regionType;
        this.accommodationType = accommodationType;
        this.regionId = regionId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnailImage = thumbnailImage;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }
}
